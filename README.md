## Medical Information Retrieval System developed for the 2017 Text REtrieval Conference (TREC) Precision Medicine Track (TREC-PM)

:construction: Documentation under construction! :construction:

## Indexing the collections

### Indexing MEDLINE
Two MEDLINE indexes are supported: a basic index which stores documents in the inverted index (i.e., as Lucene stored fields), and a Lazy index which stores JAXB-parsed articles in a separate forward index (i.e., Lucene docvalues). The Lazy JAXB-based index is preferred.

To create a lazy JAXB-based index, issue the following command:
```shell
$ JaxbMedlineIndexer INDEX_DIR [--replace] INPUT_DIR1, INPUT_DIR2, ....
```
   
This will create a Lucene index at INDEX_DIR.

### Indexing Cancer Abstracts
Execute:
```shell
ExtraCancerAbstratIndexer INDEX_DIR INPUT_DIR1, INPUT_DIR2, ...
```
    
### Indexing ClinicalTrials.gov
Execute:
```shell
ClinicalTrialIndexerCli [-n|--negate] [-D|--delete] INDEX_DIR  TRIAL_INPUT_DIR1, TRIAL_INPUT_DIR2, ...
```
    

## Running the system
Execute:
```Shell
Driver [-m|--model] [-t|--runtag] TOPICS_FILE OUTPUT_DIR
````   
where
- the `model` option is one of
  - `JOINT`: , 
  - `ASPECT_FUSION`: , 
  - `SIMILARITY_FUSION`: , 
  - `FUSION_FUSION`: ,
  - `SIMPLE`: ,
  - `STUPID`: ,
- and the `runtag` option is the runtag to be used in TREC-style submission files (e.g., UTDHLTRI)
 
### Merging runs
The different retrieval models trade recall for precision. In order to return up to 1,000 articles/trials per topic, we append the retrieved documents from multiple runs.
   
   SubmissionMerger [--runTag] [-L|--limit"] OUTPUT_FILE RUN_1, RUN_2, ...

This will produce a single output file where-in the results for each topic will be the results retrieved by RUN_1, followed by those retrieved by RUN_2, then RUN_3, etc.
