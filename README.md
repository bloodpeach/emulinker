I set this up as an eclipse project, so there an some eclipse files lying around

The Important Directories are:
- src
- src-p
- lib
- build

**src** is the source for a supposedly working version of emulinker, EmulinkerSF. It is several versions behind what is currently used on the FU servers. I obtained it from the author. I've made some modifications, but they are limited to style and comments, I've yet to change any real code.

**src-p** is a decompiled source from the version of emulinker used on the FU servers, EmulinkerX. By comparing with the original emulinker source and SF I was able to discern what most of it is doing and renamed stuff accordingly. I recently brought both src and src-p together in terms of style so they can be diff'd more easily. The differences are pretty minor except in kaillera/impl and kaillera/control, where I've yet to fully document them.

**lib** is a bunch of referenced jars. When distributed, the emulinker jar is thrown in with them.

**build** is what the distribution looks like. To build updates, only build/lib/emulinker.jar is changed. You can look through the rest of it; it's pretty self-explanatory.
