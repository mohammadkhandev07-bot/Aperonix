import Head from 'next/head'
import AssistantUI from '../components/AssistantUI'

export default function Home() {
  return (
    <>
      <Head>
        <title>Aperonix — Voice AI Assistant</title>
        <meta name="viewport" content="initial-scale=1.0, width=device-width" />
      </Head>
      <AssistantUI />
    </>
  )
}
