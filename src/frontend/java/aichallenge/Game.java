package aichallenge;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Game
{
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;

    private String name;

    private String description;

    private String className;

    @Override
    public String toString()
    {
        return "Game(" + this.name + ", " + this.description + ", " + this.className + ")";
    }

    public String getName() { return this.name; }
    public String getDescription() { return this.description; }
    public String getClassName() { return this.className; }

    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setClassName(String className) { this.className = className; }
}
