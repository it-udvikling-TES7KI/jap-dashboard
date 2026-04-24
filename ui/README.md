# UI
Selve UI er lavet med [React](https://react.dev/) og gør brug af [Vitejs](https://vitejs.dev/) som dev server og "build" tool

Det er standard eksemplet lavet med 

## Udvikling 

### Forudsætninger
- [PNPM](https://pnpm.io/)
- [Node.js + NPM](https://nodejs.org/)  (Node.js at least v16.14)
 
Det kan anbefales at bruge PNPM til at styre Nodejs.

[Tools-bin]( http://gitlab.regis.rsyd.net/jap/tools-bin) indeholder en række PowerShell Scripts til installer de nødvendige værktøjer.


### Init project og download afhængigheder
```bash
pnpm install
```

### Start dev sever
```bash
pnpm dev
```
### Prod build
```bash
pnpm build
```

Det er også muligt at anvende "targets" fra [Package.json](package.json) efter at du har afviklet `pnpm install`


## Opsætning
### API Proxy  
Når Vitejs startes med `pnpm dev` som dev server, er den konfigureret til at videre alle kald på http://localhost:8080/api til den backend som er startet op og lytter på den URL.
Dette kan ændres via [Vitejs](vite.config.js)

### Prod build
[Vitejs](vite.config.js) er sat op til at "compile" sit output til  `../build/resources/main/www`.
På den måde er det muligt at embedded dette output i den jar Gradle bygger og derved kan Micronaut vise det.(Micronaut er også sat op til at indholdet af den mappe vise)

Hvis man ønsker at test dette kan man først afvikle `pnpm build` og derefter starte Micronaut op og gå til http://localhost:8080 
