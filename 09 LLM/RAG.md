RAG is architectural pattern to interact with LLM model

Problem: We have an system that contain text file uploaded by admin or end-user. Which then user can ask the question to retrieve the answer base on the context of uploaded source file. How we can design a system like this with LLM

Normal flow:

  Send data to embedded model, we can config the number of dimension for output by parameter when sending data in to the model

  Receive array vector with float value in dimension as length

    E.g [0.2, 1.3, -0.5, ... 128/256/1024 values]

  
