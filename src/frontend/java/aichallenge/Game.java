package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Column;
import javax.persistence.OrderBy;
import java.util.Set;

@Entity
public class Game
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String name;

    @Column(columnDefinition="TEXT")
    private String description;

    private String className;

    private String jsReplayer;

    @OneToMany(mappedBy="game")
    @OrderBy("elo DESC")
    private Set<AI> AIs;

    @OneToMany(mappedBy="game")
    @OrderBy("creationDate DESC")
    private Set<Match> matches;

    @Override
    public String toString()
    {
        return "Game(" + this.name + ", " + this.description + ", " + this.className + ", " + this.jsReplayer + ")";
    }

    public String getName() { return this.name; }
    public long getId() { return this.id; }
    public String getDescription() { return this.description; }
    public String getClassName() { return this.className; }
    public String getJsReplayer() { return this.jsReplayer; }
    public Set<AI> getAIs() { return AIs; }
    public Set<Match> getMatches() { return matches; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setClassName(String className) { this.className = className; }
    public void setJsReplayer(String jsReplayer) { this.jsReplayer = jsReplayer; }
}
