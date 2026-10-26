-- NEBULA backend starter schema for Supabase PostgreSQL.
-- Review and test RLS policies before using with real users.
create extension if not exists pgcrypto;

create table if not exists public.profiles (
  id uuid primary key references auth.users(id) on delete cascade,
  username text unique not null check (char_length(username) between 3 and 24),
  display_name text not null default 'Nebula User',
  avatar_url text,
  created_at timestamptz not null default now()
);

create table if not exists public.conversations (
  id uuid primary key default gen_random_uuid(),
  title text,
  is_group boolean not null default false,
  created_by uuid not null references auth.users(id),
  created_at timestamptz not null default now()
);

create table if not exists public.conversation_members (
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  user_id uuid not null references auth.users(id) on delete cascade,
  joined_at timestamptz not null default now(),
  primary key (conversation_id, user_id)
);

create table if not exists public.messages (
  id uuid primary key default gen_random_uuid(),
  conversation_id uuid not null references public.conversations(id) on delete cascade,
  sender_id uuid not null references auth.users(id),
  body text not null check (char_length(body) between 1 and 10000),
  created_at timestamptz not null default now()
);

create index if not exists messages_conversation_created_idx
  on public.messages(conversation_id, created_at desc);

create table if not exists public.wallets (
  user_id uuid primary key references auth.users(id) on delete cascade,
  balance bigint not null default 100 check (balance >= 0),
  updated_at timestamptz not null default now()
);

create table if not exists public.wallet_transactions (
  id uuid primary key default gen_random_uuid(),
  sender_id uuid references auth.users(id),
  recipient_id uuid references auth.users(id),
  amount bigint not null check (amount > 0),
  memo text,
  created_at timestamptz not null default now()
);

-- Enable RLS by default. Add reviewed policies before exposing tables to clients.
alter table public.profiles enable row level security;
alter table public.conversations enable row level security;
alter table public.conversation_members enable row level security;
alter table public.messages enable row level security;
alter table public.wallets enable row level security;
alter table public.wallet_transactions enable row level security;

-- Intentionally no client write policies for wallets/transactions.
-- Implement transfers as a SECURITY DEFINER Postgres function or trusted Edge Function
-- with authorization checks, row locks, and an idempotency key. Never trust client balance.
-- Add policies so members can only read their conversations and messages.
