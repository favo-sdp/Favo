package ch.epfl.favo.chat;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.CompletableFuture;

import ch.epfl.favo.FakeItemFactory;
import ch.epfl.favo.chat.Model.Message;
import ch.epfl.favo.database.CollectionWrapper;
import ch.epfl.favo.database.ICollectionWrapper;
import ch.epfl.favo.util.DependencyFactory;

public class ChatUtilTest {
  private ICollectionWrapper<Message> collectionWrapper;
  private ChatUtil chatUtil;

  @Before
  public void setup() {
    collectionWrapper = Mockito.mock(CollectionWrapper.class);
    Mockito.doReturn(CompletableFuture.supplyAsync(() -> null))
        .when(collectionWrapper)
        .addDocument(Mockito.any(Message.class));
    DependencyFactory.setCurrentCollectionWrapper(collectionWrapper);

    chatUtil = ChatUtil.getSingleInstance();
  }
  @After
  public void tearDown(){
    DependencyFactory.setCurrentCollectionWrapper(null);
  }

  @Test
  public void addChatMessage() {
    chatUtil.addChatMessage(FakeItemFactory.getMessage());
  }

  @Test
  public void generateGoogleMapsPath() {
    double latitude = 0.3;
    double longitude = 0.5;
    Assert.assertNotNull(chatUtil.generateGoogleMapsPath(latitude, longitude));
  }
}
