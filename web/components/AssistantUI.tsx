import React, { useEffect, useRef, useState } from 'react'

type Message = { id: string; role: 'user'|'assistant'; content: string }

export default function AssistantUI() {
  const [state, setState] = useState<'idle'|'listening'|'thinking'|'speaking'|'error'>('idle')
  const [messages, setMessages] = useState<Message[]>(() => {
    try { const raw = localStorage.getItem('aperonix:messages'); return raw ? JSON.parse(raw) : [] } catch (e) { return [] }
  })
  const [input, setInput] = useState('')
  const recognitionRef = useRef<any | null>(null)
  const synthRef = useRef<SpeechSynthesis | null>(null)
  const [voices, setVoices] = useState<SpeechSynthesisVoice[]>([])
  const [selectedVoice, setSelectedVoice] = useState<string | null>(null)
  const [rate, setRate] = useState(1)
  const [pitch, setPitch] = useState(1)

  useEffect(() => { localStorage.setItem('aperonix:messages', JSON.stringify(messages)) }, [messages])

  useEffect(() => {
    synthRef.current = window.speechSynthesis
    const load = () => setVoices(synthRef.current?.getVoices() || [])
    load()
    window.speechSynthesis.onvoiceschanged = load
  }, [])

  function preferFemaleVoice(list: SpeechSynthesisVoice[]) {
    const ms = list.find(v => /female|woman|Microsoft/i.test(v.name))
    if (ms) return ms.name
    const anyFemale = list.find(v => /female|woman/i.test(v.name))
    return anyFemale?.name || (list[0] && list[0].name) || null
  }

  useEffect(() => {
    const pref = preferFemaleVoice(voices)
    setSelectedVoice(pref)
  }, [voices])

  const startListening = () => {
    const SpeechRecognition = (window as any).SpeechRecognition || (window as any).webkitSpeechRecognition
    if (!SpeechRecognition) { setState('error'); return }
    const rec = new SpeechRecognition()
    recognitionRef.current = rec
    rec.lang = 'en-US'
    rec.interimResults = false
    rec.onstart = () => setState('listening')
    rec.onerror = () => setState('error')
    rec.onend = () => { if (state === 'listening') setState('idle') }
    rec.onresult = (ev: any) => {
      const text = ev.results[0][0].transcript
      handleUserMessage(text)
    }
    rec.start()
  }

  const stopListening = () => {
    recognitionRef.current?.stop()
    recognitionRef.current = null
    setState('idle')
  }

  async function handleUserMessage(text: string) {
    setMessages(m => [...m, { id: Date.now().toString(), role: 'user', content: text }])
    setState('thinking')
    try {
      const resp = await fetch('/api/ai', {
        method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ message: text, history: messages })
      })
      const json = await resp.json()
      if (!json.ok) throw new Error(json.error || 'AI error')
      const reply = json.text
      setMessages(m => [...m, { id: (Date.now()+1).toString(), role: 'assistant', content: reply }])
      setState('speaking')
      speak(reply)
    } catch (e: any) {
      setState('error')
      console.error(e)
    }
  }

  function speak(text: string) {
    const s = new SpeechSynthesisUtterance(text)
    if (selectedVoice) {
      const v = voices.find(vv => vv.name === selectedVoice)
      if (v) s.voice = v
    }
    s.rate = rate
    s.pitch = pitch
    s.onend = () => setState('idle')
    window.speechSynthesis.speak(s)
  }

  function clearConversation() { setMessages([]); localStorage.removeItem('aperonix:messages') }

  return (
    <div className="min-h-screen flex flex-col items-center justify-center">
      <div className="w-full max-w-2xl p-6">
        <div className="flex flex-col items-center">
          <img src="/aperonix-logo.png" alt="Aperonix" className="w-56 h-56 object-contain" />
          <div className="mt-4 text-gray-300">{state === 'idle' ? 'Aperonix is ready' : state.toUpperCase()}</div>
        </div>

        <div className="mt-6 bg-gray-900 rounded-lg p-4">
          <div className="flex gap-4 items-center justify-center">
            <button onClick={() => state === 'listening' ? stopListening() : startListening()} className="bg-aperonix px-6 py-4 rounded-full text-white font-bold">{state === 'listening' ? 'Stop' : 'Speak'}</button>
            <button onClick={() => clearConversation()} className="px-4 py-2 border rounded text-gray-200">Clear</button>
          </div>

          <div className="mt-4">
            <label className="block text-sm text-gray-400">Fallback input</label>
            <div className="flex gap-2 mt-2">
              <input value={input} onChange={e=>setInput(e.target.value)} className="flex-1 p-2 bg-gray-800 rounded text-white" />
              <button onClick={() => { if (input.trim()) { handleUserMessage(input.trim()); setInput('') } }} className="px-4 py-2 bg-aperonix rounded">Send</button>
            </div>
          </div>

          <div className="mt-6">
            <h3 className="text-sm text-gray-400">Conversation</h3>
            <div className="mt-2 max-h-64 overflow-auto space-y-2">
              {messages.map(m => (
                <div key={m.id} className={`p-2 rounded ${m.role==='user'?'bg-gray-800 text-white':'bg-white/5 text-white'}`}>
                  <div className="text-xs text-gray-400">{m.role}</div>
                  <div className="mt-1">{m.content}</div>
                </div>
              ))}
            </div>
          </div>

          <div className="mt-6 border-t pt-4">
            <h4 className="text-sm text-gray-400">Voice & Settings</h4>
            <div className="mt-2 grid grid-cols-2 gap-4">
              <div>
                <label className="text-xs text-gray-400">Voice</label>
                <select className="w-full bg-gray-800 p-2 rounded mt-1" value={selectedVoice || ''} onChange={e=>setSelectedVoice(e.target.value)}>
                  {voices.map(v=> <option key={v.name} value={v.name}>{v.name} — {v.lang}</option>)}
                </select>
              </div>
              <div>
                <label className="text-xs text-gray-400">Speech rate</label>
                <input type="range" min="0.5" max="2" step="0.1" value={rate} onChange={e=>setRate(Number(e.target.value))} />
              </div>
              <div>
                <label className="text-xs text-gray-400">Pitch</label>
                <input type="range" min="0.5" max="2" step="0.1" value={pitch} onChange={e=>setPitch(Number(e.target.value))} />
              </div>
              <div>
                <label className="text-xs text-gray-400">AI Connection</label>
                <button onClick={async ()=>{
                  try {
                    const r = await fetch('/api/ai', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({ message: 'Ping from Aperonix Web' }) })
                    const j = await r.json();
                    if (j.ok) alert('Gemini OK')
                    else alert('Gemini error: '+j.error)
                  } catch (e:any) { alert('Connection failed: '+e.message) }
                }} className="w-full bg-gray-800 p-2 rounded mt-1">Test Connection</button>
              </div>
            </div>
          </div>

        </div>
      </div>
    </div>
  )
}
