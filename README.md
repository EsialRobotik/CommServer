# CommServer
Serveur de communication TCP entre les robots / logs / caméra / whatever


## Utilisation
- Compiler le projet avec la task Gradle shadowJar
- Lancer le serveur
- Connexion au serveur par une socket TCP sur le port 4269
- Envoyer une première ligne permettant l'identification du client

### Logger
Si la première instruction envoyé par le client est ``INFO : init logger``, le client sera identifié comme étant un LoggerThread
qui envoit des logs en continue. Ce client ne recevra jamais de retour de la part du serveur.

### LoggerListener
Si la première instruction envoyé par le client est ``loggerListener``, le client sera identifié comme un LoggerListener
qui souhaite recevoir toutes les informations des Logger enregistré sur le serveur.

### Robot
Si la première instruction envoyé par le client est ``robot``, le client sera identifié comme étant un Robot qui enverra
des instructions aux autres Robot. Le serveur ne renvoit pas ses propres instructions à un Robot mais les distribue à
tout les autres.


### Echo
Si aucune des instructions précédentes n'est utilisées, le serveur utilisera le mode Echo par défaut qui renvoit simplement
les instructions envoyé par le client à lui même.