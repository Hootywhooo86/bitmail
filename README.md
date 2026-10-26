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

- **Swipe like Gmail:** swipe a conversation right to delete, left to archive (both with UNDO). Either direction can be changed in ⚙ Settings to archive, delete, read/unread, star or nothing, with an optional "ask before deleting".
- **Inbox tabs:** Primary, Promotions, Social, Updates, Everything.
- **Filters (combine any):** Unread, Starred, Attachments, Newsletters, Receipts & bills, From people, This week, Big files. They're Gmail search queries underneath, so they work on the whole mailbox, and with the search bar.
- **💸 Subscriptions:** scans the last year of mail for subscriptions, receipts, trials and mailing lists, grouped by sender. Each one has one-tap Unsubscribe (uses the sender's own `List-Unsubscribe`: one-click when offered, otherwise the unsubscribe email or page), Archive all and Delete all.
- **✨ AI (Claude):** with your own Anthropic API key (Settings), the AI
  - sorts the subscription scan into paid, free trials, newsletters and marketing, with prices, monthly total, what to cancel and how,
  - answers questions about your inbox ("what bills are due?", "summarize my unread"),
  - summarizes an email, pulls out action items, drafts replies, and turns notes into an email.
- Everything else: folders and labels with unread counts, threaded reading (HTML mail in a script-blocked sandbox), attachments, star/mark unread/restore, compose/reply/reply all/forward, keyboard shortcuts, Inbox Zero quest, 🎨 colours, demo inbox.

### AI privacy and cost

- The key is stored only in this browser (`localStorage`) and requests go directly from the browser to `api.anthropic.com` (official `@anthropic-ai/sdk`, pinned to 0.133.0, loaded only when an AI feature is used). Settings requires a consent tick before any AI call.
- What is sent: for the subscription review, a per-sender summary (address, name, count, last date, up to 4 subjects and 2 previews), never full emails; for Ask AI, sender/subject/date/preview of your latest 60 conversations; for summarize/reply, the open thread's text.
- Default model is Claude Opus 5.5; Sonnet 5.5 and Haiku 5.5 can be picked in Settings for lower cost. Each answer shows its token count and estimated price. Opus/Sonnet requests opt into Anthropic's server-side refusal fallback (`fallbacks: "default"`).
- Email text is treated as untrusted: the AI only reads and advises, and every action (unsubscribe, archive, delete, send) is a button you press.

### Connecting Gmail (one-time)

Google requires an OAuth Client ID for any app that reads Gmail:

1. In https://console.cloud.google.com create a project and enable the **Gmail API**.
2. Set up the OAuth consent screen (External). While the app is in "Testing", add each Gmail address that will use it as a test user.
3. Credentials → Create credentials → OAuth client ID → **Web application**. Under Authorized JavaScript origins add `https://hootywhooo86.github.io`.
4. Paste the Client ID into the app's setup screen, or set `DEFAULT_CLIENT_ID` in `app/index.html` so nobody has to paste it.

Scopes used: `gmail.modify` (read, label, archive, trash) and `gmail.send`. Because these are restricted scopes, publishing for the general public needs Google's verification. Testing mode covers you and up to 100 test users.

## Android app

`android/` is a [Trusted Web Activity](https://developer.chrome.com/docs/android/trusted-web-activity): the APK opens the live web app (https://hootywhooo86.github.io/bitmail/app/) in Chrome, full-screen with its own icon. It's built this way because Google blocks Gmail sign-in inside an app's built-in web view; inside Chrome, sign-in and everything else work as on the web, and updates to the site reach the app instantly. GitHub Actions builds the APK on every push (`.github/workflows/android.yml`); pushes to `main` publish it at https://github.com/Hootywhooo86/bitmail/releases/tag/android-latest (direct download: https://github.com/Hootywhooo86/bitmail/releases/download/android-latest/bitmail.apk).

**Hide Chrome's address bar (optional, one-time):** Android shows a small URL bar until the site vouches for the app. To remove it, publish this file at `https://hootywhooo86.github.io/.well-known/assetlinks.json`. That's the root of the `Hootywhooo86.github.io` repository, which needs to exist with GitHub Pages on:

```json
[{
  "relation": ["delegate_permission/common.handle_all_urls"],
  "target": {
    "namespace": "android_app",
    "package_name": "com.hootywhooo86.bitmail",
    "sha256_cert_fingerprints": ["5C:78:82:D1:91:86:06:3F:CE:43:68:52:EF:87:75:F7:97:68:B7:FD:17:41:26:B5:4F:C1:8E:F4:A0:D3:6E:B4"]
  }
}]
```

- Build it yourself: `cd android && ./gradlew assembleDebug` (JDK 21 + Android SDK). `tools/make_icons.py` regenerates the pixel icons.
- Needs Android 7.0+ and an internet connection (sign-in and mail are online anyway).
- Signed with `android/app/sideload.keystore`, a key committed to the repo for sideloading; the fingerprint above belongs to it. A Play Store release needs a private key kept out of the repo (and its fingerprint added to the file).

## Status

- Landing page: live, links to the app.
- App: v2 live (`app/index.html`): swipe actions, filters, subscription finder, Claude AI assistant. Needs a Google OAuth Client ID to connect real Gmail (demo inbox works without one) and an Anthropic API key for AI features.
