package nl.rug.escher.entities.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;

import javax.jdo.Extent;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import nl.rug.escher.entities.Endpoint;
import nl.rug.escher.gui.Main;

import org.junit.Before;
import org.junit.Test;

public class PersistenceTest {
	@Before
	public void setUp() {
		File f = new File("test.db");
		if (f.exists() && !f.delete()) {
			throw new RuntimeException();
		}
	}
	
	@Test
	public void testPersistEndpoint() {
		PersistenceManagerFactory pmf = getFactory();
		PersistenceManager pm = pmf.getPersistenceManager();
	
	    Endpoint endpoint = Main.buildDefaultEndpoint();
	    try {
			Transaction tx = pm.currentTransaction();
			try {
			    tx.begin();
			    pm.makePersistent(endpoint);
			    tx.commit();
			} finally {
			    if (tx.isActive()) {
			        tx.rollback();
			    }
			}
			
			tx = pm.currentTransaction();
			try {
				tx.begin();
				
				Extent<Endpoint> extent = pm.getExtent(Endpoint.class);
				Iterator<Endpoint> it = extent.iterator();
				assertTrue(it.hasNext());
				assertEquals(endpoint, it.next());
				assertFalse(it.hasNext());
				tx.commit();
			} finally {
			    if (tx.isActive()) {
			        tx.rollback();
			    }
			}
	    } finally {
		    pm.close();
	    }
	}

	private PersistenceManagerFactory getFactory() {
		return JDOHelper.getPersistenceManagerFactory("datanucleus.test.properties");
	}
}
