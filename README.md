# Cosmic Player Deceleration

Stops slower. Adds some deceleration instead of almost immediately stopping for
supported versions of Cosmic Reach.

## Downloads

Cosmic Player Deceleration is only officially available on CRMM and GitHub. All
published version of this mod are both in
[CRMM](https://www.crmm.tech/mod/cosmic-player-deceleration/versions) and [GitHub Releases](https://github.com/StartsMercury/cosmic-player-deceleration/releases):

> * <https://www.crmm.tech/mod/cosmic-player-deceleration/versions>
> * <https://github.com/StartsMercury/cosmic-player-deceleration/releases>

## Video Demonstration

Following are videos and applies to supported versions of Cosmic Reach.

### With Mod

https://github.com/user-attachments/assets/f6df18b9-4ae8-45df-8980-735f07c78e5f

### Without Mod

https://github.com/user-attachments/assets/70a3e9e3-9433-4683-8efa-9a7d83c080b9

---

This repository is generated from
https://codeberg.org/CRModders/cosmic-quilt-example.

## Wiki

For a wiki on how to use Cosmic Quilt & Quilt, please look at the [Cosmic Quilt
wiki] .

## How to test/build

For testing in the developer environment, you can use the `./gradlew run` task

For building, the usual `./gradlew build` task can be used. The mod jars will be
in the `build/libs/` folder

## Notes
- Most project properties can be changed in the <tt>[gradle.properties]</tt>
- To change author, description and stuff that is not there, edit <tt>[src/main/resources/quilt.mod.json]</tt>
- The project name is defined in <tt>[settings.gradle.kts]</tt>
- To add Quilt mods in the build, make sure to use `internal` rather than `implementation`

[src/main/resources/quilt.mod.json]: src/main/resources/quilt.mod.json
[gradle.properties]: gradle.properties
[settings.gradle.kts]: settings.gradle.kts

[Cosmic Quilt wiki]: https://codeberg.org/CRModders/cosmic-quilt/wiki
