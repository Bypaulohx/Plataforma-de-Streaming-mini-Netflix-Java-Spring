## Funcionalidades
- Upload de vídeos (multipart/form-data).
- Armazenamento no sistema de arquivos (pasta `uploads` por padrão).
- Metadados dos vídeos salvos em banco H2 em memória.
- Listagem de catálogo (JSON) e UI simples para visualização e reprodução com `<video>` (streaming com suporte a Range requests).
- Endpoint para download do arquivo.

## Pré-requisitos
- Java 17+ (JDK 17)
- Maven 3.6+
- VSCode (opcional) com extensão Java (recomendado)

## Como executar (passo a passo)
1. Faça o download do repositório (ZIP fornecido neste pacote) e extraia.
2. Abra a pasta `mini-netflix` no VSCode ou terminal.
3. Execute (pelo terminal):
```bash
# compilar e executar com o plugin do Spring Boot
mvn spring-boot:run
```
ou
```bash
mvn package
java -jar target/mini-netflix-0.0.1-SNAPSHOT.jar
```

4. Abra o navegador em: `http://localhost:8080` — você verá a interface de upload e catálogo.
5. Para testes via curl (exemplo):
```bash
# upload
curl -v -F file=@/caminho/para/video.mp4 -F title="Meu Vídeo" http://localhost:8080/api/videos

# listar vídeos
curl http://localhost:8080/api/videos
```

## Endpoints importantes
- `POST /api/videos` — envia um arquivo (campo `file`) + `title` + `description`
- `GET /api/videos` — lista metadados (JSON)
- `GET /api/videos/{id}` — metadados de um vídeo
- `GET /api/videos/{id}/stream` — endpoint para streaming (usado pelo player)
- `GET /api/videos/{id}/download` — força download do arquivo

## Arquitetura (resumo)
- **Controller**: recebe uploads e fornece endpoints REST.
- **Service**: lógica de armazenamento (filesystem) e manipulação de metadados.
- **Repository**: JPA + H2 em memória (facilita rodar sem banco externo).
- **Frontend**: arquivos estáticos em `src/main/resources/static` (index.html + app.js + styles.css).
