d:
cd \
cd composestar
make clean
del d:\ComposestarInstaller\binaries\*.dll
del d:\ComposestarInstaller\binaries\*.exe
del d:\ComposestarInstaller\binaries\Composestar.jar
make src
make jar
copy Composestar.jar d:\ComposestarInstaller\binaries
copy *.exe d:\ComposestarInstaller\binaries
cd ..
cd ComposestarInstaller
"C:\Program Files\NSIS\makensis.exe" ComposeStar.nsi
cd \
cd composestar