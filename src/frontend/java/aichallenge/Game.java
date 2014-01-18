package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.Set;

@Entity
public class Game
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String name;

    private String description;

    private String className;

    @OneToMany(mappedBy="game")
    private Set<AI> AIs;

    @Override
    public String toString()
    {
        return "Game(" + this.name + ", " + this.description + ", " + this.className + ")";
    }

    public String getName() { return this.name; }
    public long getId() { return this.id; }
    public String getDescription() { return this.description; }
    public String getClassName() { return this.className; }
    public Set<AI> getAIs() { return AIs; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setClassName(String className) { this.className = className; }
}
