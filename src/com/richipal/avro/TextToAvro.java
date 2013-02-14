package com.richipal.avro;

import java.io.IOException;

import org.apache.avro.Schema;
import org.apache.avro.mapred.AvroJob;
import org.apache.avro.mapred.AvroWrapper;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reporter;

import com.vibrantmedia.analytics.schema.avro.Student;

public class TextToAvro {
	
	public static class TextToAvroMapper 
	extends MapReduceBase implements Mapper<Text, Text, AvroWrapper<Student>, NullWritable> {
		Integer studentId = 0;
		
		public void map(
		        Text key, Text value,
		        OutputCollector<AvroWrapper<Student>, NullWritable> collector,
		        Reporter reporter)
		      throws IOException {
			String[] fields = value.toString().split("\t");
			
			try{
				studentId = Integer.parseInt(key.toString());
				}catch(Exception e){studentId = 0;}
				Student student = new Student();
				student.setStudentId(studentId);
				student.setFirstName(fields[0]);
				student.setLastName(fields[1]);
				student.setAddress(fields[2]);
			collector.collect(new AvroWrapper<Student>(student),  NullWritable.get());
		};
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception{
		Path inputPath = new Path(args[0]);
		Path outputPath = new Path(args[1]);
		
		JobConf job = new JobConf(TextToAvro.class);
		outputPath.getFileSystem(job).delete(outputPath, true);
		
		Schema schema =ReflectData.get().getSchema(Student.class);
	    job.setJobName("TextToAvro");

	    job.setInputFormat(KeyValueTextInputFormat.class);
	    job.setMapperClass(TextToAvroMapper.class);
	    job.setNumReduceTasks(0);
	    AvroJob.setOutputSchema(job, schema);

	    FileInputFormat.setInputPaths(job, inputPath);
	    FileOutputFormat.setOutputPath(job, outputPath);
	    JobClient.runJob(job);
	}

}
