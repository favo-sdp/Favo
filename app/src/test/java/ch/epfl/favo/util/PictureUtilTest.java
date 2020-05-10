package ch.epfl.favo.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;

public class PictureUtilTest {
  private PictureUtil pictureUtil;
  private FirebaseStorage mockStorage;

  @Before
  public void setup() {

    mockStorage = Mockito.mock(FirebaseStorage.class);
    StorageReference mockRef = Mockito.mock(StorageReference.class);
    StorageReference mockChildRef = Mockito.mock(StorageReference.class);
    Mockito.doReturn(mockChildRef).when(mockRef).child(anyString());
    Task returnTask = Mockito.mock(Task.class);
    Mockito.doReturn(returnTask).when(mockChildRef).delete();
    Mockito.doReturn(mockRef).when(mockStorage).getReference();
    DependencyFactory.setCurrentFirebaseStorage(mockStorage);
    pictureUtil = PictureUtil.getInstance();
  }

  @After
  public void tearDown() {
    DependencyFactory.setCurrentFirebaseStorage(null);
  }

  @Test
  public void uploadPicture() {}

  @Test
  public void deletePicture() {
    String sampleUrl =
        "https://firebasestorage.googleapis.com/v0/b/favo-11728.appspot.com/o/V6Y8F6DOR3NKW71UEQKULUPXMQC0.jpeg?alt=media&token=f88ee85f-a201-435f-88cd-4b5803df9656";
    pictureUtil.deletePicture(sampleUrl);
  }

  @Test
  public void downloadPicture() {}
}
