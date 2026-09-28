# OffxVauLt

**Your memories. Your vault.**  
**Anonymous Access**

Premium mobile-first private-gallery simulation for owner-provided photos and videos.

## Access flow

1. Intro screen.
2. Mandatory Anonymous Access popup.
3. Popup password.
4. Vault form: Mobile/Email, Phone Model, Secret Code.
5. Vault loading animation.
6. Media loads **one item at a time** with an **Accessing...** screen.
7. After the configured reveal count, **SYSTEM IS LOCKED** appears.
8. Unlock code reveals the remaining owner-provided media one by one.
9. Gallery supports image/video cards, fullscreen viewer, swipe, next/previous and download.
10. Finish → THANK YOU ❤️ → THANKS FOR VISITING.

## Demo/config passwords

These are intentionally client-side demo values and are visible in the browser/source:

- Main popup: `OFFX-START-2026`
- OffxVauLt unlock: `OFFX-UNLOCK-001`
- AccessBySomesh unlock: `ACCESS-UNLOCK-002`
- HaCkeR-sOmesh unlock: `SOMESH-UNLOCK-003`

Change them in `offxvault/script.js` before publishing if desired. Do **not** treat client-side passwords as real security.

## Gallery codes

- `OffxVauLt`
- `AccessBySomesh`
- `HaCkeR-sOmesh`

Related aliases are included as empty placeholders and can be populated in the same config.

## Adding 100+ photos/videos

Static GitHub/Vercel hosting cannot safely enumerate a folder's contents from browser JavaScript. Therefore each owner-provided file is listed in the gallery's `media` array.

Example:

```js
media: [
  {type:"image",src:"gallery/OffxVauLt/01.jpg"},
  {type:"image",src:"gallery/OffxVauLt/02.jpg"},
  {type:"video",src:"gallery/OffxVauLt/03.mp4"},
  {type:"image",src:"gallery/OffxVauLt/04.webp"}
  // continue with 100+ entries as needed
]
```

There is no hard 10-item gallery limit. `revealCount: 10` only controls when the simulated SYSTEM IS LOCKED screen appears.

Put files in the matching folder:

- `offxvault/gallery/OffxVauLt/`
- `offxvault/gallery/AccessBySomesh/`
- `offxvault/gallery/HaCkeR-sOmesh/`

## Privacy behavior

The site does not read a visitor's device gallery, camera, contacts, cloud storage or personal accounts. The Mobile/Email and Phone Model fields are local UI fields only and are not transmitted by this static implementation.

## Branding

Developer: **Somesh Koli**  
Instagram: **@offx.somesh**

## Deployment

Deploy the repository/folder using the existing Vercel/GitHub setup. No Supabase dependency is required for this client-side simulation.
