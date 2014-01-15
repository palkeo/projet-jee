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
}
