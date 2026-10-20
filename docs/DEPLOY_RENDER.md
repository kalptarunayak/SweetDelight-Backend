# Deploy to Render with Supabase PostgreSQL

1. **Create Supabase Project**: Go to [supabase.com](https://supabase.com), create a new project, copy `Database URL`, `User`, `Password`, and `JWT Secret` (from Project Settings > API).
2. **Create Web Service on Render**: In Render Dashboard, click **New + > Web Service**, connect this Git repository, and set Root Directory to `backend`.
3. **Select Docker Environment**: Set Runtime to **Docker** (Render will automatically detect `/backend/Dockerfile`).
4. **Configure Environment Variables**:
   - `DATABASE_URL`: `jdbc:postgresql://<db-host>.supabase.co:5432/postgres` (or direct Supabase URI)
   - `DB_USER`: `postgres`
   - `DB_PASSWORD`: `<your-supabase-db-password>`
   - `SUPABASE_JWT_SECRET`: `<your-supabase-jwt-secret>`
   - `RAZORPAY_KEY_ID`: `<your-razorpay-key-id>`
   - `RAZORPAY_KEY_SECRET`: `<your-razorpay-key-secret>`
   - `RAZORPAY_WEBHOOK_SECRET`: `<your-razorpay-webhook-secret>`
   - `COD_ENABLED`: `true`
   - `NOTIFY_MODE`: `console` (or `whatsapp`)
   - `CORS_ORIGINS`: `https://your-frontend.pages.dev,http://localhost:5173`
   - `SWAGGER_ENABLED`: `true`
5. **Deploy**: Click **Create Web Service**. Flyway runs migrations automatically on boot. Open `https://<your-render-app>.onrender.com/swagger-ui.html` to test!
