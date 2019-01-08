package com.itheima.lucene;

import com.itheima.lucene.dao.BookDao;
import com.itheima.lucene.dao.impl.BookDaoImpl;
import com.itheima.lucene.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class IndexManager {


    @Test
    public void createIndex() {

       BookDao bookDao = new BookDaoImpl();
       List<Book> bookList = bookDao.findAll();
       try {
           List<Document> documents = new ArrayList<>();
           for (Book book : bookList) {
               Document document = new Document();
               document.add(new TextField("id",book.getId() + "", Field.Store.YES));
               document.add(new TextField("bookName",book.getBookName(), Field.Store.YES));
               document.add(new TextField("price",book.getPrice() + "", Field.Store.YES));
               document.add(new TextField("pic",book.getPic(), Field.Store.YES));
               document.add(new TextField("bookDesc",book.getBookDesc(), Field.Store.YES));
               documents.add(document);
           }
           Analyzer analyzer = new IKAnalyzer();
           IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2,analyzer);
           Directory directory = FSDirectory.open(new File("C:\\Users\\Minne\\Desktop\\Lucene\\index_db"));
           IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
           for (Document document : documents) {
               indexWriter.addDocument(document);
               indexWriter.commit();
           }
           indexWriter.close();
       } catch (Exception e) {
           e.printStackTrace();
       }
    }



    @Test
    public void searchIndex() throws Exception {
        Analyzer analyzer = new IKAnalyzer();
        QueryParser queryParser = new QueryParser("bookName",analyzer);
        Query query = queryParser.parse("java");
        Directory directory = FSDirectory.open(new File("C:\\Users\\Minne\\Desktop\\Lucene\\index_db"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        TopDocs topDocs = indexSearcher.search(query, 10);
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("文档分数" + scoreDoc.score);
            System.out.println("文档索引" + scoreDoc.doc);
            System.out.println("_____________");
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println("图书id" + doc.get("id"));
            System.out.println("图书名字" + doc.get("bookName"));
            System.out.println("图书价格" + doc.get("price"));
            System.out.println("图片" + doc.get("pic"));
            System.out.println("图书描述" + doc.get("bookDesc"));
            System.out.println("==========================================");
        }
        indexReader.close();
    }

    public void deleteIndex() throws Exception {
        Directory directory = FSDirectory.open(new File("C:\\Users\\Minne\\Desktop\\Lucene\\index_db"));
        IKAnalyzer ikAnalyzer = new IKAnalyzer();
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_2,ikAnalyzer);
        // 创建IndexWriter对象
        IndexWriter indexWriter = new IndexWriter(directory,indexWriterConfig);
    }
}
