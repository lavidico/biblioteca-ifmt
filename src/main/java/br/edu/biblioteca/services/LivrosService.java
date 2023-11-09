package br.edu.biblioteca.services;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import br.edu.biblioteca.entities.Livro;
import br.edu.biblioteca.exceptions.RegistroNaoEncontradoException;
import br.edu.biblioteca.repositories.LivrosRepository;

@Service
public class LivrosService {

  private final LivrosRepository livrosRepository;
  
  public LivrosService(LivrosRepository livrosRepository) {
    this.livrosRepository = livrosRepository;
  }

  @CacheEvict(value = "livros", allEntries = true)
  public Livro criarLivro(Livro novoLivro) {
    livrosRepository.save(novoLivro);
    return novoLivro;
  }
  
  @Cacheable(value = "livros")
  public List<Livro> listarLivros(String filtro) {
    return livrosRepository.findAllByTituloContainingIgnoreCase(filtro);
  }

  @Cacheable(value = "livro", key="#id")
  public Livro buscarLivro(String id) {
    Livro livro = livrosRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException("erro.registroNaoEncontradoComId", id));

    return livro;
  }
  
  @Caching(
		   evict = { @CacheEvict(value = "livros", allEntries = true) },
		   put = { @CachePut(value = "livro", key="#id") })
  public Livro editarLivro(String id, Livro livroEditado) {
    Livro livroSelecionado = livrosRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException("erro.registroNaoEncontradoComId", id));

    livroSelecionado.setTitulo(livroEditado.getTitulo());
    livroSelecionado.setAutor(livroEditado.getAutor());

    livrosRepository.save(livroSelecionado);

    return livroSelecionado;
  }

  @Caching(evict = { 
		  @CacheEvict(value = "livro", key = "#id"), 
		  @CacheEvict(value = "livros", allEntries = true) })
  public void excluirLivro(String id) {
    Livro livro = livrosRepository
      .findById(id)
      .orElseThrow(() -> new RegistroNaoEncontradoException("erro.registroNaoEncontradoComId", id));

    livrosRepository.delete(livro);
  }
}
