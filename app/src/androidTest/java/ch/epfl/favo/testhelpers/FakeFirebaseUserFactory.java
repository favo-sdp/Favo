package ch.epfl.favo.testhelpers;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzff;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;

import java.util.List;

public class FakeFirebaseUserFactory {

  public static FirebaseUser createFirebaseUser(
      String displayName, String email, Uri photo, String provider) {
    return new FirebaseUser() {
      @NonNull
      @Override
      public String getUid() {
        return null;
      }

      @NonNull
      @Override
      public String getProviderId() {
        return provider;
      }

      @Override
      public boolean isAnonymous() {
        return false;
      }

      @Nullable
      @Override
      public List<String> zza() {
        return null;
      }

      @Override
      public List<? extends UserInfo> getProviderData() {
        return null;
      }

      @NonNull
      @Override
      public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
        return null;
      }

      @Override
      public FirebaseUser zzb() {
        return null;
      }

      @NonNull
      @Override
      public FirebaseApp zzc() {
        return null;
      }

      @Nullable
      @Override
      public String getDisplayName() {
        return displayName;
      }

      @Nullable
      @Override
      public Uri getPhotoUrl() {
        return photo;
      }

      @Nullable
      @Override
      public String getEmail() {
        return email;
      }

      @Nullable
      @Override
      public String getPhoneNumber() {
        return null;
      }

      @Nullable
      @Override
      public String zzd() {
        return null;
      }

      @NonNull
      @Override
      public zzff zze() {
        return null;
      }

      @Override
      public void zza(@NonNull zzff zzff) {}

      @NonNull
      @Override
      public String zzf() {
        return null;
      }

      @NonNull
      @Override
      public String zzg() {
        return null;
      }

      @Nullable
      @Override
      public FirebaseUserMetadata getMetadata() {
        return null;
      }

      @NonNull
      @Override
      public zzz zzh() {
        return null;
      }

      @Override
      public void zzb(List<zzy> list) {}

      @Override
      public void writeToParcel(Parcel dest, int flags) {}

      @Override
      public boolean isEmailVerified() {
        return false;
      }
    };
  }
}
