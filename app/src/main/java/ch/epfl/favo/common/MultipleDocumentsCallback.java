package ch.epfl.favo.common;

import java.util.List;
import java.util.Map;

public interface MultipleDocumentsCallback {
  void onCallback(List<Map> values);
}
