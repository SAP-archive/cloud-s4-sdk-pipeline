### Support autoscaling using Kubernetes

The pipeline can leverage certain Kubernetes features to support dynamic scalability.  

#### Solutions assessed
- Create pod per step
- Create pod per stage
- Create pod per pipeline

##### Create pod per step
The idea was to create a pod whenever `dockerExecute` step is invoked. 
Though it was a simple solution, it came with a huge overhead of additional stashing and unstashing. 
This noticeably delayed the pipeline execution. 
Hence this solution is not appropriate in our use case.

##### Create pod per stage
Here we follow the approach of creating a pod per each stage.
This addresses the problem we had in the first approach. 
But, we need to know the `dockerImage`s used in each stage in advance so that a pod can be created with those images. 
Since the list of `dockerImage`s of an individual stage is derived from the `pipeline_config.yaml`, there needs to be an approach which can support the pipeline extensions.

##### Create pod per pipeline
We have also analyzed the idea of using a pod per pipeline. 
However, this would contradict our basic principle of isolation between stages, because the file system is shared by every container that exists in a pod.

#### Conclusion
We have decided to use the second approach where a pod is created per stage. 
To support the same when pipeline extensions are used, we will create a pod per step for extensions.
