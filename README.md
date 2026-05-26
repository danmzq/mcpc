# MCPC Storage Automation (Fabric + Baritone)

Mod ini adalah orchestrator automation storage berbasis movement player nyata.

## Fitur utama
- Register `source` dan `destination` chest via command.
- Simpan config permanen ke JSON.
- Scan chest via iterasi `BlockEntity` loaded chunks.
- Analisis inventory chest.
- Queue transfer task source -> destination.
- Bridge ke Baritone (`goto`) untuk navigation.

## Command
- `/storage setsource <id>`
- `/storage setdest <id>`
- `/storage additem <minecraft:item>`
- `/storage scan <radius>`
- `/storage exportscan`
- `/storage runonce`

## Catatan
- Implementasi ini fokus pada arsitektur core system dan orchestration loop.
- Integrasi click-inventory bisa diperluas untuk handling edge-case server specific.
