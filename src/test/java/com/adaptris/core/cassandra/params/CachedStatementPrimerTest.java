package com.adaptris.core.cassandra.params;

import junit.framework.TestCase;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CachedStatementPrimerTest extends TestCase {
  
  @Mock private Session mockSession;
  
  @Mock private PreparedStatement mockPreparedStatement;
  
  private CachedStatementPrimer statementPrimer;
  
  public void setUp() throws Exception {
    statementPrimer = new CachedStatementPrimer();
    MockitoAnnotations.initMocks(this);
    
    when(mockSession.prepare(anyString())).thenReturn(mockPreparedStatement);
  }
  
  public void tearDown() throws Exception {
    
  }
  
  public void testNewStatementAdded() throws Exception {
    String stringStatement = "select * from myTable";
    
    assertEquals(0, statementPrimer.getStatements().size());
    
    statementPrimer.prepareStatement(mockSession, stringStatement);
    
    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
  }
  
  public void testDuplicateNotAdded() throws Exception {
    String stringStatement = "select * from myTable";
    
    assertEquals(0, statementPrimer.getStatements().size());
    
    statementPrimer.prepareStatement(mockSession, stringStatement);
    
    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
    
    statementPrimer.prepareStatement(mockSession, stringStatement);
    statementPrimer.prepareStatement(mockSession, stringStatement);
    
    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
  }
  
  public void testNewMultipleStatementsAdded() throws Exception {
    String stringStatement = "select * from myTable";
    String stringStatement2 = "select * from myTable2";
    
    assertEquals(0, statementPrimer.getStatements().size());
    
    statementPrimer.prepareStatement(mockSession, stringStatement);
    
    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
    
    statementPrimer.prepareStatement(mockSession, stringStatement2);
    
    assertEquals(2, statementPrimer.getStatements().size());
  }
  
  public void testCacheLimit() throws Exception {
    statementPrimer.setCacheLimit(1);
    
    String stringStatement = "select * from myTable";
    String stringStatement2 = "select * from myTable2";
    
    assertEquals(0, statementPrimer.getStatements().size());
    
    statementPrimer.prepareStatement(mockSession, stringStatement);
    
    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
    
    statementPrimer.prepareStatement(mockSession, stringStatement2);
    
    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement2, statementPrimer.getStatements().get(0).getStringStatement());
  }

}
