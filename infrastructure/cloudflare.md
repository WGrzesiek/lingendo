1. Instalacja cloudflared
# Pobranie i instalacja (Ubuntu/Debian)
``` bash 
curl -fsSL https://github.com/cloudflare/cloudflared/releases/latest/download/cloudflared-linux-amd64.deb -o cloudflared.deb
sudo dpkg -i cloudflared.deb
```

# Sprawdzenie wersji
``` bash 
cloudflared --version
```

2. Logowanie do Cloudflare
``` bash 
   cloudflared tunnel login
```
- Otwórz link w przeglądarce
- Zaloguj się na konto Cloudflare
- Wybierz strefę: lingendo.app
- Po tym kroku pojawi się ~/.cloudflared/cert.pem

3. Utworzenie tunelu
``` bash 
   cloudflared tunnel create lingendo-tunnel
```

Zapamiętaj:

nazwę tunelu: lingendo-tunnel

ścieżkę do JSON z credkami (np. /home/wawrzen/.cloudflared/5b7148a2bc81317.json)

4. Konfiguracja Nginx trzeba dodac oprocz localhosta
``` bash 
server_name lingendo.app www.lingendo.app localhost;
```

5. Plik konfiguracyjny tunelu
   sudo mkdir -p /etc/cloudflared
   sudo nano /etc/cloudflared/config.yml

Przykład dla lingendo.app:
``` bash 
tunnel: lingendo-tunnel
credentials-file: /home/wawrzen/.cloudflared/5b7148a2bc81317.json

ingress:
- hostname: lingendo.app
  service: http://localhost:80
- hostname: www.lingendo.app
  service: http://localhost:80
- service: http_status:404
```

6. Powiązanie tunelu z DNS w Cloudflare
``` bash 
   cloudflared tunnel route dns lingendo-tunnel lingendo.app
   cloudflared tunnel route dns lingendo-tunnel www.lingendo.app
```

7. Uruchomienie tunelu jako usługi
``` bash    
    sudo cloudflared service install
   sudo systemctl enable cloudflared
   sudo systemctl start cloudflared
   sudo systemctl status cloudflared
```

8. Sprawdzenie logów tunelu
``` bash 
   sudo journalctl -u cloudflared -f
```

9. Wyłączenie tunelu
``` bash 
   sudo systemctl stop cloudflared
```
