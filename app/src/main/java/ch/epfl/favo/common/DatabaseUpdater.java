package ch.epfl.favo.common;

import java.util.Map;

public interface DatabaseUpdater {
  void addDocument(String key, Map document);
}
