import java.io.IOException;
import java.util.StringTokenizer;
import java.util.HashMap;
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

public class InvertedIndexJob {

  public static class InvertIndexMapper extends Mapper<Object,Text,Text,Text>{
    private Text word=new Text();
    private Text document=new Text(); 

    public void map(Object key,Text value,Context context) throws IOException,InterruptedException{
      String [] splitted=value.toString().split("\t",2);
      document.set(splitted[0]);
      String filtered=splitted[1].toLowerCase();
      filtered=filtered.replaceAll("[^a-z]"," ");

      StringTokenizer itr=new StringTokenizer(filtered.toString());
      while(itr.hasMoreTokens()){
        word.set(itr.nextToken());
        context.write(word, document);
      }
    }
  }

  public static class InvertIndexReducer extends Reducer<Text,Text,Text,Text>{
    private Text result=new Text();
    public void reduce(Text key,Iterable<Text> values,Context context) throws IOException, InterruptedException{
      HashMap<String,Integer> stats=new HashMap<String,Integer>();

      for(Text val:values){
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
    Job job=Job.getInstance(conf,"Inverted_Index");

    FileInputFormat.addInputPath(job,new Path(args[0]));
    FileOutputFormat.setOutputPath(job,new Path(args[1]));

    job.setJarByClass(InvertedIndexJob.class);
    job.setMapperClass(InvertIndexMapper.class);
    job.setReducerClass(InvertIndexReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    job.waitForCompletion(true);
  }
}