-- ============================================================
-- SUPABASE DATABASE MIGRATION SCRIPT FOR PAYMENT APP
-- Copy and paste this script into your Supabase SQL Editor.
-- ============================================================

-- 1. Create 'users' table
CREATE TABLE IF NOT EXISTS public.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    uid VARCHAR(9) UNIQUE NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    password TEXT NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    credit_balance NUMERIC DEFAULT 100.0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 2. Create 'transactions' table
CREATE TABLE IF NOT EXISTS public.transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_uid VARCHAR(50) NOT NULL,
    receiver_uid VARCHAR(50) NOT NULL,
    sender_phone VARCHAR(50) NOT NULL,
    receiver_phone VARCHAR(50) NOT NULL,
    amount NUMERIC NOT NULL,
    type VARCHAR(20) NOT NULL DEFAULT 'SENT', -- 'SENT', 'RECEIVED', 'QR_REDEEM'
    description TEXT,
    created_at VARCHAR(50) DEFAULT TO_CHAR(NOW(), 'DD Mon, HH12:MI AM')
);

-- 3. Create 'qr_codes' table
CREATE TABLE IF NOT EXISTS public.qr_codes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    qr_code_id VARCHAR(100) UNIQUE NOT NULL,
    credit_amount NUMERIC NOT NULL,
    is_redeemed BOOLEAN DEFAULT FALSE,
    redeemed_by_uid VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- 4. Enable Row Level Security (RLS) & Add Public Access Policies
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Allow public select on users" ON public.users FOR SELECT USING (true);
CREATE POLICY "Allow public insert on users" ON public.users FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update on users" ON public.users FOR UPDATE USING (true);

ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Allow public select on transactions" ON public.transactions FOR SELECT USING (true);
CREATE POLICY "Allow public insert on transactions" ON public.transactions FOR INSERT WITH CHECK (true);

ALTER TABLE public.qr_codes ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Allow public select on qr_codes" ON public.qr_codes FOR SELECT USING (true);
CREATE POLICY "Allow public insert on qr_codes" ON public.qr_codes FOR INSERT WITH CHECK (true);
CREATE POLICY "Allow public update on qr_codes" ON public.qr_codes FOR UPDATE USING (true);
