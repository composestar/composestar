h:
cd \
cd composestar
make clean
del h:\ComposestarInstaller\binaries\*.dll
del h:\ComposestarInstaller\binaries\*.exe
del h:\ComposestarInstaller\binaries\Composestar.jar
make src
make jar
copy Composestar.jar h:\ComposestarInstaller\binaries
copy *.exe h:\ComposestarInstaller\binaries
cd ..
cd ComposestarInstaller
"H:\NSIS\makensis.exe" ComposeStar.nsi
cd \
cd composestar
