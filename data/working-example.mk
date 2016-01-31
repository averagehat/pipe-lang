import Bio 
exec cutadapt

paired.bam <- *_R1_*.trimmed, *_R2_*.trimmed, <ref.fasta> [bash]
	echo "do some bash"
	echo "do some more bash"

*_R1_*.filtered, *_R2_*.filtered <- *_R1_*.fastq, *_R2_*.fastq
	echo "do some bash"

merged.bam <- paired.bam, ?unpaired.bam [python]
	print "do some python"

.filtered, .filtered <-  *_R1_*.fastq, *_R2_*.fastq
	echo "example with extensions"
#A comment
