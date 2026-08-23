# Archive: route-remove-city-id

Date: 2026-08-23  
Branch: dev  
Commits: 05f3342..23525ad  

## Summary
Completely remove the `cityId` association from the route module across the admin API, app API, frontend, and database schema. The frontend now uses free-text map input instead of city selection. No city association is retained.

## Artifacts
- Admin DB migration: `love-space-admin/src/main/resources/db/changelog/changes/016-remove-route-city-id.sql`
- Admin backend: removed `cityId` field from `Route` entity and related DTOs/controllers/services/repositories
- App backend: removed `CityRepository` usage in route queries and stopped returning `cityName`
- Web frontend: `Routes/Form.tsx` now uses free-text `mapName` input; unified wording to `所属地图/关联地图` across pages

## Notes
- Database schema change requires archival per openspec-session-protocol §5/§6.
