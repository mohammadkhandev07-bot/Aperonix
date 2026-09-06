# Vercel

To deploy Aperonix (web) to Vercel:

1. In Vercel dashboard create a new project from this GitHub repository.
2. In Project Settings > Environment Variables, add:
   - GEMINI_API_KEY = <your_gemini_api_key>
   - GEMINI_MODEL = gemini-1.0 (optional, default used)
3. Deploy. Vercel will run `npm install` and `npm run build` in `web/` if configured as monorepo. If using the `web` directory as root, configure accordingly.

Local development:

1. cd web
2. npm install
3. Create `.env.local` with:
   GEMINI_API_KEY=your_key_here
   GEMINI_MODEL=gemini-1.0
4. npm run dev

Security:
- Never commit real API keys. Add them as Vercel Environment Variables.
- The API route `/api/ai` reads the key server-side and never exposes it to the browser.
