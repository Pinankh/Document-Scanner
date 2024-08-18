package com.camscanner.paperscanner.pdfcreator.common.p018db;

import android.content.Context;
import java.util.ArrayList;
import java.util.Iterator;
import com.camscanner.paperscanner.pdfcreator.common.utils.ImageStorageUtils;
import com.camscanner.paperscanner.pdfcreator.model.Document;

public class DocumentRepository {
    public static void removeDocumentWithChilds(Context context, Document document) {
        ArrayList<Document> toRemove = new ArrayList<>();
        DBManager.getInstance().getDocuments(toRemove, document.uid);
        toRemove.add(document);
        Iterator<Document> it = toRemove.iterator();
        while (it.hasNext()) {
            removeDocument(context, it.next());
        }
    }

    public static void removeDocument(Context context, Document doc) {
        DBManager.getInstance().deleteDocument(doc);
        ImageStorageUtils.deleteImages(context, doc.getPaths(), true);
    }
}
