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

## The app (`app/`)

A pixel Gmail client. It reads and sends your real Gmail straight from the browser through the Gmail API. No 128bit server sees your mail. Live at https://hootywhooo86.github.io/bitmail/app/

- Inbox with Primary / Social / Promotions / Updates tabs, plus Starred, Snoozed, Important, Sent, Drafts, All Mail, Spam, Trash and your own labels, with unread counts.
- Gmail search syntax in the search bar (`from:mom has:attachment`).
- Threaded reading (HTML mail renders in a sandboxed frame with scripts blocked), attachment downloads.
- Star, archive, delete (both with UNDO), mark unread, restore from trash.
- Compose, reply, reply all, forward.
- Keyboard shortcuts: `c` compose, `/` search, `e` archive, `#` delete, `s` star, `j`/`k` next/previous, `Esc` close.
- Inbox Zero quest (+50 XP), installable PWA, demo inbox for trying it without an account.

### Connecting Gmail (one-time)

Google requires an OAuth Client ID for any app that reads Gmail:

1. In https://console.cloud.google.com create a project and enable the **Gmail API**.
2. Set up the OAuth consent screen (External). While the app is in "Testing", add each Gmail address that will use it as a test user.
3. Credentials → Create credentials → OAuth client ID → **Web application**. Under Authorized JavaScript origins add `https://hootywhooo86.github.io`.
4. Paste the Client ID into the app's setup screen, or set `DEFAULT_CLIENT_ID` in `app/index.html` so nobody has to paste it.

Scopes used: `gmail.modify` (read, label, archive, trash) and `gmail.send`. Because these are restricted scopes, publishing for the general public needs Google's verification. Testing mode covers you and up to 100 test users.

## Status

- Landing page: live, links to the app.
- App: v1 working (`app/index.html`); needs a Google OAuth Client ID to connect real Gmail (demo inbox works without one).
