package app.fuxion.surya.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import app.fuxion.surya.entities.GithubHistory;

@Repository
public interface GithubHistoryRepository extends JpaRepository<GithubHistory, String> {
  
}