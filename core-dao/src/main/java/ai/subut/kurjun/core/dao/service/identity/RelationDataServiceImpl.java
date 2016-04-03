package ai.subut.kurjun.core.dao.service.identity;


import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ai.subut.kurjun.core.dao.api.identity.RelationDAO;
import ai.subut.kurjun.core.dao.api.identity.RelationObjectDAO;
import ai.subut.kurjun.model.identity.Relation;
import ai.subut.kurjun.model.identity.RelationObject;
import ninja.jpa.UnitOfWork;


/**
 *
 */
@Singleton
public class RelationDataServiceImpl implements RelationDataService
{

    private RelationDAO relationDAO;
    private RelationObjectDAO relationObjectDAO;

    @Inject
    public RelationDataServiceImpl(RelationDAO relationDAO, RelationObjectDAO relationObjectDAO)
    {
        this.relationDAO = relationDAO;
        this.relationObjectDAO = relationObjectDAO;
    }


    //***************************
    @UnitOfWork
    @Override
    public void persistRelation( Relation relation )
    {
        try
        {
            if(relation != null)
                relationDAO.persist( relation );
        }
        catch ( Exception ex )
        {
        }
    }


    //***************************
    @UnitOfWork
    @Override
    public Relation mergeRelation( Relation relation )
    {
        try
        {
            if(relation != null)
                return relationDAO.merge( relation );
        }
        catch ( Exception ex )
        {
            return null;
        }

        return null;
    }


    //***************************
    @UnitOfWork
    @Override
    public Relation getRelation( long relationId)
    {
        try
        {
            return relationDAO.find( relationId );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getAllRelations()
    {
        try
        {
            return relationDAO.findAll( "RelationEntity" );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getRelationsBySource( String sourceObjId, int sourceObjType )
    {
        try
        {
            return relationDAO.findBySourceObject( sourceObjId, sourceObjType);
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getRelationsBySource( RelationObject relationObject )
    {
        try
        {
            return relationDAO.findBySourceObject( relationObject.getObjectId() , relationObject.getType());
        }
        catch ( Exception ex )
        {
            return null;
        }

    }

    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getRelationsByTarget( String targetObjId, int targetObjType )
    {
        try
        {
            return relationDAO.findBySourceObject( targetObjId, targetObjType);
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getRelationsByTarget( RelationObject relationObject )
    {
        try
        {
            return relationDAO.findByTargetObject( relationObject.getObjectId(), relationObject.getType() );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getRelationsByTrustObject( String trustObjId, int trustObjType )
    {
        try
        {
            return relationDAO.findByTrustObject( trustObjId, trustObjType );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public List<Relation> getRelationsByTrustObject( RelationObject relationObject )
    {
        try
        {
            return relationDAO.findByTrustObject( relationObject.getObjectId(), relationObject.getType() );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public Relation getTrustObjectOwner( String objectId, int objectType )
    {
        try
        {
            return relationDAO.findTrustObjectOwner( objectId, objectType );
        }
        catch ( Exception ex )
        {
            return null;
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public void removeByTrustObject( String objectId, int objectType )
    {
        try
        {
            relationDAO.removeByTrustObject( objectId, objectType );
        }
        catch ( Exception ex )
        {
        }
    }



    //***************************
    @UnitOfWork
    @Override
    public void removeRelation( Relation entity )
    {
        try
        {
            relationDAO.remove( entity );
        }
        catch ( Exception ex )
        {
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public void removeRelation( long id)
    {
        try
        {
            Relation relation = relationDAO.find( id );

            if(relation != null)
            {
                relationDAO.remove(relation  );
            }
        }
        catch ( Exception ex )
        {
        }

    }


    //***************************
    @UnitOfWork
    @Override
    public RelationObject getRelationObject( RelationObject relationObject )
    {
        try
        {
            return relationObjectDAO.find( relationObject.getUniqueId());

        }
        catch ( Exception ex )
        {
            return null;
        }
    }


    //***************************
    @UnitOfWork
    @Override
    public RelationObject getRelationObject( String uniqID, int type )
    {
        try
        {
            return relationObjectDAO.findByUniqID( uniqID, type );

        }
        catch ( Exception ex )
        {
            return null;
        }
    }


    //***************************
    @UnitOfWork
    @Override
    public void removeRelationObject( String uniqID, int type )
    {
        try
        {
            RelationObject obj = getRelationObject( uniqID, type);

            if(obj != null)
            {
                removeRelationObject( obj );
            }
        }
        catch ( Exception ex )
        {
        }
    }


    //***************************
    @UnitOfWork
    @Override
    public void removeRelationObject( RelationObject relationObject )
    {
        try
        {
            if(relationObject != null)
            {
                relationObjectDAO.remove( relationObject  );
            }
        }
        catch ( Exception ex )
        {
        }
    }


}