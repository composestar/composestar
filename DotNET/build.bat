@echo off
cd Core&&cd compiletime&&make clean&&make&&cd ..&&cd runtime&&make clean&&make&&cd ..&&cd..&&cd DotNet&&cd compiletime&&make&&cd ..&&cd runtime&&make clean&&make&&cd ..&&cd plugin&&cd VSAddin&&make&&cd ..&&cd ..&&cd ..


