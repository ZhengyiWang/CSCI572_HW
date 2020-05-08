import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.naming.Context;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class InvertedIndexBigrams {

  public static class InvertedIndexMapper extends Mapper<Object, Text, Text, Text>{
    private Text word=new Text();
    private Text document=new Text();
    
    public static List<String> bigrams(String str) {
        List<String> bigrams = new ArrayList<String>();
        String[] words=str.split(" ");
        for(int i=0;i<words.length-1;i++)
            bigrams.add(concat(words,i,i+1));
        return bigrams;
    }

    public static String concat(String[] words,int start,int end){
        StringBuilder sb=new StringBuilder();
        for (int i=start;i<=end;i++)
            sb.append((i>start ? " " : "")+words[i]);
        return sb.toString();
    }
    
    public void map(Object key,Text value,Context context) throws IOException,InterruptedException{
      String [] splitted=value.toString().split("\t",2);
      document.set(splitted[0]);
      String filtered=splitted[1].toLowerCase();
      filtered=filtered.replaceAll("[^a-z]"," ");

      for(String bigram:bigrams(filtered)){
          word.set(bigram);
          context.write(word,document);
      }
    }
  }

  public static class InvertedIndexReducer extends Reducer<Text,Text,Text,Text>{
    private Text result=new Text();
    public void reduce(Text key,Iterable<Text> values,Context context) throws IOException, InterruptedException{ 
      HashMap<String,Integer> stats=new HashMap<String,Integer>();
      for (Text val:values){
        stats.put(val.toString(),stats.getOrDefault(val.toString(),0)+1);
      }

      String together=new String("");
      for(String doc_id:stats.keySet()){
        together=together+doc_id+":"+String.valueOf(stats.get(doc_id))+"\t";
      }

      result.set(together);
      context.write(key,result);
    }
  }

  public static void main(String[] args) throws Exception{
    Configuration conf=new Configuration();
    Job job=Job.getInstance(conf, "Inverted_Index");

    FileInputFormat.addInputPath(job,new Path(args[0]));
    FileOutputFormat.setOutputPath(job,new Path(args[1]));

    job.setJarByClass(InvertedIndexBigrams.class);
    job.setMapperClass(InvertedIndexMapper.class);
    job.setReducerClass(InvertedIndexReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    job.waitForCompletion(true);
  }
}