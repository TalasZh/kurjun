package ai.subut.kurjun.metadata.common;


import java.util.List;

import ai.subut.kurjun.model.metadata.Dependency;
import ai.subut.kurjun.model.metadata.RelationOperator;


/**
 * Simple serializable POJO implementation of {@link Dependency}.
 *
 */
public class DefaultDependency implements Dependency
{
    private String packageName;
    private List<Dependency> alternatives;
    private String version;
    private RelationOperator relationOperator;


    @Override
    public String getPackage()
    {
        return packageName;
    }


    public void setPackage( String packageName )
    {
        this.packageName = packageName;
    }


    @Override
    public List<Dependency> getAlternatives()
    {
        return alternatives;
    }


    public void setAlternatives( List<Dependency> alternatives )
    {
        this.alternatives = alternatives;
    }


    @Override
    public String getVersion()
    {
        return version;
    }


    public void setVersion( String version )
    {
        this.version = version;
    }


    @Override
    public RelationOperator getDependencyOperator()
    {
        return relationOperator;
    }


    public void setRelationOperator( RelationOperator relationOperator )
    {
        this.relationOperator = relationOperator;
    }


}
