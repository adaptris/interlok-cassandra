package com.adaptris.core.cassandra.params;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.adaptris.interlok.util.Closer;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class CachedStatementPrimerTest {

  @Mock
  private Session mockSession;

  @Mock
  private PreparedStatement mockPreparedStatement;

  private AutoCloseable openMocks;

  private CachedStatementPrimer statementPrimer;

  @Before
  public void setUp() throws Exception {
    statementPrimer = new CachedStatementPrimer();
    openMocks = MockitoAnnotations.openMocks(this);

    when(mockSession.prepare(anyString())).thenReturn(mockPreparedStatement);
  }

  @After
  public void tearDown() throws Exception {
    Closer.closeQuietly(openMocks);
  }

  @Test
  public void testNewStatementAdded() throws Exception {
    String stringStatement = "select * from myTable";

    assertEquals(0, statementPrimer.getStatements().size());

    statementPrimer.prepareStatement(mockSession, stringStatement);

    assertEquals(1, statementPrimer.getStatements().size());
    assertEquals(stringStatement, statementPrimer.getStatements().get(0).getStringStatement());
  }

  @Test
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

  @Test
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

  @Test
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
