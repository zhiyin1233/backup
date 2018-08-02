echo off
cd /d %~dp0

git add .
:begin
set input=
set /p input=please input you commit message:

echo
echo your input commit message is：%input%


git commit -m "%input%"
git push

pause
