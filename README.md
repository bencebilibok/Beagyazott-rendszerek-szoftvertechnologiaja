# SnakeGame

Az UDP-üzenetek továbbítása a Multiplayer Snake játékhoz
---------------------------------------------------------------

Telepítés és konfiguráció
A játék elérhető a github címen, innen letölthető egy .zip csomagként. Az Intellij Idea fejlesztői környezetben az „Open Folder” opciót használva egyszerűen meg lehet nyitani a teljes tároló mappáját a játéknak, így minden hozzá tartozó kép és fájl egy helyen lesz a környezetben.
Egy másik opció a github használata a fejlesztői környezetben, ezzel még egyszerűbben elérhető a program, így az adott linkről le kell másolnunk a programot a „git clone <repository-http>-t. Itt figyelnünk kell, hogy mire van beállítva a projekt, mert az sem kizárt, hogy SSH kulcs segítségével tudjuk elérni.
	A konfiguráció létrehozása megvalósítható a „Select Run/Debug Configuration ” menüfül alól, itt az „Edit Configuration”-ra kattintva egy új konfiguráció hozható létre. A bal felső sarokban találhato „+” ikonra kattinta egy „Application”-t szeretnénk hozzá adni. Ennek az SDK-ját (Software Development Kit) szükséges megadnunk és hogy melyik osztályban található a main classunk, ahonnan minden más osztályt meghív a programunk. Adhatunk egy nevet a konfigurációnak, a mi esetünkben lesz egy konfiguráció a szervernek és egy a klienseknek. Tehát lehet ez például „Server” és „Client”.

  
**A játék működése**
  
  A játék egyből a pálya képernyővel indul, ahol egy felső menüsorban ki lesz írva, hogy épp hányadik játékosként csatlakozott a játékos. Ekkor egy számláló indul el a képernyőn, ami minden csatlakozott kígyó után elindul 5 másodperctől lefelé számolva. 
  A következő fázisban elindul a játék. Különböző eszközökről különböző játékosok csatlakozhatnak, ezeknek csak a saját kígyójuk lesz látható a képernyőn.
  A játék a kígyó halálával végződik, ha neki ütközik a saját testének. Ebben az esetben a mozgása leáll, az alma nem generálódik új helyre és a képernyőre kiírodik, hogy „A játék véget ért, itt vannak az eredmények: <pontszám>”:
  Ezután a játék véget ért. Minden játékosnak meg lesz jelenítve a saját képernyőjén a saját eredménye, a játékosok ezt később egyeztethetik, a győztes így derül ki.

