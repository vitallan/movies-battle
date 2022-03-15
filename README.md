# Movies Battle

Movies Battle é uma API Rest que serve como backend para um jogo de competição entre filmes. O jogador recebe duas opções e precisa adivinhar qual dos filmes é melhor avaliado pela crítica e pelos outros expectadores. Se errar três vezes, o jogador precisa iniciar um novo jogo. É disponibilizado um ranking para decidir qual jogador possui a melhor pontuação ao longo do tempo.

### Tecnologias usadas:
* Java 17
* Spring-Boot (web, jpa, security)
* H2
* Jsoup
* Swagger 
* JWT para autenticação

### Executando o Projeto

O projeto segue com o maven embarcado e usa banco de dados h2, então basta baixar e executar o install

``./mvnw clean install``

isso executará os testes automatizados. Em caso de sucesso:

``./mvnw spring-boot:run``

executará o servidor na porta 8080. Durante o startup da aplicação, serão buscados filmes na pagina do imdb via web scraping para popular o banco de dados com filmes para serem utilizados como opções no jogo.

### Documentação (WIP)

Com a aplicação no ar, a documentação dos endpoints se encontra no link:

``http://localhost:8080/swagger-ui/``

A documentação ainda não está plenamente executável, então uma opção mais agradável é usar o postman para testar as chamadas. A pasta ``postman`` contem o json com as chamadas já configuradas, sendo necessário apenas importar na ferramenta. Caso tenha dúvidas, [esse link](https://learning.postman.com/docs/getting-started/importing-and-exporting-data/) pode ajudar.

## Observações (ou "o que eu faria se tivesse mais tempo")

#### 1. Documentação não ideal

O ideal seria centralizar a documentação num lugar só que seja plenamente funcional e integrada com o Spring-security (adicionando os headers nas chamadas de forma automática, por exemplo). Também colocar em português ao invés de inglês.

#### 2. Testes unitários

Embora tenha testado o cerne da lógica da criaçao de batalhas, esses testes poderiam ser expandidos. Alguns endpoints também não possuem testes, e nem todos os caminhos de exceções mapeadas tem teste. Web scraper também não possui testes. Não cheguei a confirmar, mas não deve ter batido 80% de cobertura.

#### 3. Nem todas as exceções tratadas

Fiz uma passada geral no final, mas tem exceções que ainda não possuem tratamento padronizado.

#### 4. Ranking sendo calculado manualmente

Uma query de banco resolveria de forma muito mais simples, rápida e elegante, só que eu estava gastando tempo criando a query sem entidade e haviam outras funcionalidades primárias que ainda não estavam funcionando. Fiz na mão só por questão de tempo.

#### 5. Configurações em constantes

Ao invés de application.properties, algumas configurações ainda estão em constantes.

#### 6. Autenticação complexa

O mecanismo de segurança ficou mais complexo do que o desejado e menos funcional do que o necessário. Isso fez com que algumas validações que poderiam ser diretas no framework de segurança precisassem ser feitas manualmente. O manuseio do JWT também ficou manual.

#### 7. Empates sendo evitados

Em caso de empate entre a nota de dois filmes, precisaria ter uma lógica a mais. Evitei isso fazendo o scraper ignorar filmes que contenham notas iguais as ja cadastradas no banco.

#### 8. IDs pulando numeros

Algum comportamento estranho do h2.

#### 9. Baixo nivel de maturidade RESTful

Seria interessante um maior cuidado na gestão dos endpoints para aumentar o nivel de maturidade. Utilizar algum media-type também ajudaria na documentação e na navegabilidade da API.
