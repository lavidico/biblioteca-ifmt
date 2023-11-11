# Projeto Final - Desenvolvimento de Software Através de Frameworks @ IFMT/2023
- **Biblioteca/Framework:** 
	- [Spring Boot Framework / Spring Integration (Cache)](http://google.com)

- __Tecnologias aplicadas:__  <br/>
  - Java  <br/>
  - Spring Boot Framework

- **Integrantes:**
  - Jardel Ribeiro
  - Lavídico Alves de Brito Junior
  <br/>


## 1. Descrição da biblioteca/framework

Biblioteca provedora de cache para uma aplicação baseada no framework Spring Boot, esta implementação permite o uso de várias soluções alternativas de cache como providers sem impacto no código por exemplo, EhCache, Hazelcast, Infinispan, Redis entre outras.

A implementação utilizada é baseada na especificação [JSR 107:JCache (Java Temporary Caching API)](https://www.jcp.org/en/jsr/detail?id=107)


## 2. Descrição do problema

Quando um sistema possui consultas e/ou processamentos custosos em termos de recurso (memória, cpu, rede, banco de dados, tempo de resposta) cujo requisição e resposta tendem a ser recorrentes ocasionando baixa performance ou consumo excessivo dos recursos citados. 

Especialmente em casos onde os custos de operação são baseados no consumo desses recursos, se faz necessário a utilização de ferramentas que otimizem os resultados.


## 3. Solução

Implementação da biblioteca de Cache para o Spring Boot Framework, aplicado aos resultados de consultas de livros.

O cache foi implementado na camada de serviço reduzindo o acesso direto ao banco de dados, aumentando a performance da aplicação diminuindo o tempo de resposta.

Para utilizar a biblioteca de cache disponibilizada pelo Spring, são necessários os seguintes passos:

1. Adicionar a dependencia ao projeto: <br/>
`implementation group: org.springframework.boot', name: 'spring-boot-starter-cache'`
		
2. Habilitar o uso no escopo da aplicação utilizando a seguinte anotação na classe principal: <br />
```
@EnableCaching
@SpringBootApplication
public class BibliotecaApplication {...
```
3. Na camada de serviço, definir quais funções devem ter seus resultados armazenados em cache:
```
@Cacheable(value = "livros")
public List<Livro> listarLivros(String filtro) {
   return livrosRepository.findAllByTituloContainingIgnoreCase(filtro);
  }
  
@Cacheable(value = "livro", key="#id")
public Livro buscarLivro(String id) {
  Livro livro = livrosRepository
      .findById(id)
      .orElseThrow(() -> new ` `RegistroNaoEncontradoException("erro.registroNaoEncontradoComId", id));
    return livro;
  }
```
  
  A anotação @Cacheable indica a aplicação que o retorno desta função deve ser armazanada em memória (cache) sendo identificada pelo nome "Livros", definida pelo atributo value. Em uma próxima requisição este método não será executado pois o seu resultado da função será buscado no cache.
  
  
4. Nas funções/procedimentos que possam afetar a integridade dos dados em cache , devem ser tratados conforme cada caso:

- Para remover o cache obrigando a função a ser executada na próxima requisição, utiliza-se a anotação @CacheEvict:
```
  @CacheEvict(value = "livros", allEntries = true)
  public void excluirLivro(String id) {
    Livro livro = livrosRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException("erro.registroNaoEncontradoComId", id));

    livrosRepository.delete(livro);
  }
```
Onde o cache a ser removido é definido pelo parametro value, o atributo allEntries, indica que todo o conteúdo armazenado é excluido.

- Para atualizar os dados em cache após executar a função/procedimento, utiliza-se a anotação @CachePut: 
```
 @CachePut(value = "livro", key="#id") })
  public Livro editarLivro(String id, Livro livroEditado) {
    Livro livroSelecionado = livrosRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException("erro.registroNaoEncontradoComId", id));

    livroSelecionado.setTitulo(livroEditado.getTitulo());
    livroSelecionado.setAutor(livroEditado.getAutor());

    livrosRepository.save(livroSelecionado);

    return livroSelecionado;
  }
```  
  O parametro value indica o cache a ser utilizado e o parametro key indica qual item único será afetado. 

  

## 4. Referências

[Documentação Spring Boot Framework (Cache Abstraction)](https://docs.spring.io/spring-framework/reference/integration/cache.html)
<br/>
[Apresentação](https://docs.google.com/presentation/d/1Iuq8UrJailj9e3cglvYSz9OFPitgLEQxFa4hv5u_zLc/edit?usp=sharing)
