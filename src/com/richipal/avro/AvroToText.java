package com.richipal.avro;

import java.io.IOException;
import java.util.Map;

import org.apache.avro.mapred.AvroAsTextInputFormat;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.mapred.TextOutputFormat;
import org.json.simple.parser.JSONParser;

public class AvroToText {


	public static class AvroToTextMapper extends MapReduceBase implements Mapper<Text, Text, Text, NullWritable> {
		JSONParser parser = new JSONParser();
		@SuppressWarnings("rawtypes")
		Map fieldMap = null;

		@SuppressWarnings({"rawtypes" })
		public void map(Text key, Text value, OutputCollector<Text, NullWritable> collector, Reporter reporter) throws IOException {
			NullWritable nullWritable = NullWritable.get();

			try {
				fieldMap = (Map) parser.parse(key.toString());
				Text map = new Text(fieldMap.get("studentId") + "\t" +
						fieldMap.get("firstName").toString() + "\t" + 
								fieldMap.get("lastName").toString() + "\t" +
								fieldMap.get("address").toString() + "\t" );
				collector.collect(map, nullWritable);
			}catch(Exception e){
				e.printStackTrace();
			}

		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		JobConf job = new JobConf(AvroToText.class);;
		Path inputPath = new Path(args[0]);
		Path outputPath = new Path(args[1]);
		
		outputPath.getFileSystem(job).delete(outputPath, true);
		
		job.setJobName("AvroToText");

		job.setInputFormat(AvroAsTextInputFormat.class);
		job.setMapperClass(AvroToTextMapper.class);
		job.setOutputFormat(TextOutputFormat.class);
		job.setNumReduceTasks(0);

		FileInputFormat.setInputPaths(job, inputPath);
		FileOutputFormat.setOutputPath(job, outputPath);
		JobClient.runJob(job);
	}

}
