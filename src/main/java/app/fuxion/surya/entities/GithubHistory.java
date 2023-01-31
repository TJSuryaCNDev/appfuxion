package app.fuxion.surya.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "GithubHistory")
public class GithubHistory {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "name", nullable = false)
  private String name;
  
  @Column(name = "file", nullable = false)
  private String file;
  
  @Column(name = "download", nullable = false)
  private String download;

  @Column(name = "at", nullable = false)
  private Date at;

  public GithubHistory() {

  }

  public GithubHistory(String name) {
    this.name = name;
  }

  public GithubHistory(String name, String file) {
    this.name = name;
    this.file = file;
  }

  public long getId() {
    return id;
  }
  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }

  public String getFile() {
    return file;
  }
  public void setFile(String file) {
    this.file = file;
  }

  public String getDownload() {
    return download;
  }
  public void setDownload(String download) {
    this.download = download;
  }

  public Date getAt() {
    return at;
  }
  public void setAt(Date at) {
    this.at = at;
  }
 
}