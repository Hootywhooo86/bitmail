# bitmail — Mail, but chunkier.

The 128bit family's pixel email app. Landing page + working mock live at https://hootywhooo86.github.io/bitmail/

## What it is

A chunky, satisfying email app for the 128bit family: a readable inbox with folders and search, tap-to-read messages, starring and archiving, a compose flow, and an "Inbox Zero" quest that feeds XP into 128bitlife. The mock on the landing page does all of that (with sample messages).

## Relationship to bitmessenger

bitmessenger is the chat app (real-time conversation, stickers, streaks). bitmail is the mailbox (asynchronous, threads, triage, quests). They share the family feed but serve different rhythms: chat for now, mail for later.

## Architecture

- One 128bit account + the shared family event feed (`supabase/family-feed.sql` — same `family_events` schema as the rest of the family). Mail emits `mail.received` / `mail.sent` / `inbox.zero` events so quests and the family timeline can subscribe.
- Landing page is a static mock only; the real app build is Carlos's lane (see below).

## Open product questions (Daniel's call)

- **What carries the mail?** The big one: a standalone 128bit-account mail network, or an IMAP/SMTP client that pulls in Gmail/Outlook/etc.? (Rocky recommends starting as a client for existing mailboxes — reach first, network later — but it's Daniel's call.)
- **Family inbox:** a shared household inbox layer, or strictly per-account mail?
- **Triage:** how smart does the priority sorting get in v1 — rules, or learned ranking?
- **Spam:** built-in filtering, or rely on the provider's?
- **E2EE:** end-to-end encryption day one for family mail, or later?
- **bitcalendar tie-in:** do meeting invites and deadlines flow into bitcalendar automatically?
- **Platform:** iOS/Android/both, native build or wrapped web app?

## Status

- Landing page + mock: live (coming-soon).
- Real app build: not started. Formspree ID still needed in the waitlist form (`YOUR_FORM_ID` placeholder with a local-confirm fallback).
