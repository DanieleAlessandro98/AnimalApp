## AnimalApp

![logo](https://i.imgur.com/w6RHyFy.png)

### Descrizione
AnimalApp è un sistema progettato per semplificare la gestione quotidiana degli animali domestici e facilitare la risoluzione di problemi sociali legati agli animali, come l'identificazione degli animali smarriti, l'adozione facilitata e l'accesso ai servizi veterinari e agli enti di assistenza.

### Caratteristiche Principali
- Identificazione e tracciamento degli animali smarriti o rubati
- Adozione facilitata tramite il collegamento tra potenziali adottanti e rifugi
- Funzionalità di login e registrazione con gestione dei profili utente
- Visualizzazione e gestione del profilo utente con informazioni sugli animali posseduti, visite mediche e spese sostenute
- Creazione, modifica ed eliminazione di profili utente, visite mediche e spese
- Schermate dedicate per la visualizzazione delle segnalazioni e delle richieste degli utenti
- Ricerca di veterinari e enti registrati nell'app in base alla vicinanza geografica
- Utilizzo dei principi del Material Design per un'interfaccia coerente e intuitiva

### Entità Coinvolte
- **Utente**: Gestisce il proprio profilo, le segnalazioni, le richieste e l'interazione con veterinari e enti.
- **Veterinario**: Fornisce servizi veterinari e può essere contattato dagli utenti per prenotare visite.
- **Ente**: Offre servizi di assistenza agli animali e può essere contattato dagli utenti per adozioni o altre necessità.

### Tecnologie Utilizzate
- **Android Studio** per lo sviluppo dell'applicazione Android
- **Firebase** per la gestione del database, dell'autenticazione utente e dello storage dei file
- **Google Play Services Maps** per l'integrazione delle funzionalità di Google Maps nell'applicazione

### Dipendenze del Progetto
- **com.journeyapps:zxing-android-embedded:4.3.0**: ZXing è una libreria open-source per la scansione di codici a barre e QR code nell’applicazione.
- **com.google.code.gson:gson:2.8.8**: Libreria di Java per la serializzazione e deserializzazione di oggetti JSON.
- **com.google.android.material:material:1.8.0**: Libreria Material Design Components di Google.
- **com.google.android.gms:play-services-maps:18.2.0**: Utilizzata per integrare le funzionalità di Google Maps in un'applicazione Android.
- **com.google.firebase**:
  - **firebase-storage:20.2.0**: Libreria di Firebase Storage nell'ecosistema Firebase per lo sviluppo mobile.
  - **firebase-auth:22.0.0**: Libreria di Firebase Authentication nell'ecosistema Firebase per lo sviluppo mobile.
  - **firebase-bom:32.0.0**: La BOM di Firebase è un file di metadati che elenca le versioni consigliate delle diverse librerie Firebase e le relative dipendenze.
  - **firebase-analytics**: Utilizzata per integrare il servizio di analisi Firebase Analytics con l'applicazione.
  - **firebase-firestore:24.1.1**: Libreria di Firestore per Firebase nell'ecosistema Android.

### Dati di accesso
Per scopi di test e presentazione del progetto, sono state create le seguenti utenze:
- **Privato - Ash Ketchum**
  - Email: proprietario1@gmail.com
  - Password: Test123
- **Privato - Misty Williams**
  - Email: (da inserire)
  - Password: (da inserire)
- **Veterinario - InfermieraJoy**
  - Email: joy@gmail.com
  - Password: 123456
- **Ente - PensionePokemon**
  - Email: pensionepokemon10@gmail.com
  - Password: 123456
