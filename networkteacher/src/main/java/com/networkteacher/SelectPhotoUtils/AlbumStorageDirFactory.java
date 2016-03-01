package com.networkteacher.SelectPhotoUtils;

import java.io.File;

/**
 * Created by AJ on 10/6/15.
 */
public abstract class AlbumStorageDirFactory {
    public abstract File getAlbumStorageDir(String albumName);
}
