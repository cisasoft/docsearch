package org.jab.docsearch;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InitAdapter {
	
	public static void updateIndex(String searchtext,String iname, String path, String indexPath, String archiveDir, int depth) throws Exception{
		DocSearch ds = new DocSearch();
		//删除索引
		ds.removeIndex(indexPath);
		//创建索引
		ds.createIndex( iname,  path,  indexPath,  archiveDir,  depth);
	}
	
	public static List<InitAdapterMeta> searching(String searchtext,String iname, String path, String indexPath, String archiveDir, int depth){
		DocSearch ds = new DocSearch();
		return ds.doSearching( searchtext, iname,  path,  indexPath,  archiveDir,  depth);
	}
	
	public static void main(String[] args) throws Exception {
    	String iname = "upload";
    	String path = "D:\\upload";
    	String indexPath = "D:\\project\\docsearch\\.docSearcher\\indexes\\upload";
    	String archiveDir = "D:\\project\\docsearch\\.docSearcher\\archives";
    	int depth = 4;
    	String searchtext = "菲律宾";
    	
		DocSearch ds = new DocSearch();
		//删除索引
		ds.removeIndex(indexPath);
		//创建索引
		ds.createIndex( iname,  path,  indexPath,  archiveDir,  depth);
		//开始检索
		List<InitAdapterMeta> iaml = ds.doSearching( searchtext, iname,  path,  indexPath,  archiveDir,  depth);
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
		System.out.println("main--------->"+iaml.size());
		for(int i=0;i<iaml.size();i++){
			System.out.println(iaml.get(i).toString());
			String str = iaml.get(i).getContent();
			String dest = null;
			if (str!=null) {
	            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
	            Matcher m = p.matcher(str);
	            dest = m.replaceAll(" ");
	            iaml.get(i).setContent(dest);
	        }
			System.out.println(iaml.get(i).toString());
		}
		System.out.println("------------------------------------------------------");
		System.out.println("------------------------------------------------------");
	}

}
