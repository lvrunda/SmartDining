'''Train CIFAR10 with PyTorch.'''
from __future__ import print_function

import torch
from torch.autograd import Variable
import torchvision
import torchvision.transforms as transforms
import os
import matplotlib.pyplot as plt
import numpy as np

from models import *

# classes = None

# def predict_image(image):
#     image_tensor = test_transforms(image).float()
#     image_tensor = image_tensor.unsqueeze_(0)
#     input = Variable(image_tensor)
#     input = input.to(device)
#     output = model(input)
#     index = output.data.cpu().numpy().argmax()
#     return index
#
# def get_random_images(num):
#     data = torchvision.datasets.ImageFolder(data_dir, transform=test_transforms)
#     classes = data.classes
#     indices = list(range(len(data)))
#     np.random.shuffle(indices)
#     idx = indices[:num]
#     from torch.utils.data.sampler import SubsetRandomSampler
#     sampler = SubsetRandomSampler(idx)
#     loader = torch.utils.data.DataLoader(data,
#                    sampler=sampler, batch_size=num)
#     dataiter = iter(loader)
#     images, labels = dataiter.next()
#     return images, labels
# data_dir = 'food/test2'
# input_size = 224
# normalize = transforms.Normalize(mean=[0.485, 0.456, 0.406],
#                                  std=[0.229, 0.224, 0.225])
# test_transforms = transforms.Compose([
#         transforms.RandomResizedCrop(input_size, scale=(1.0, 1.0)),
#         transforms.ToTensor(),
#         normalize,
#     ])
#
# device = torch.device('cpu')
# model = MobileNetV2(n_class=2)
# model.load_state_dict(torch.load('checkpoint/ckpt.pth', map_location=device))
#
# data = torchvision.datasets.ImageFolder(data_dir, transform=test_transforms)
# classes = data.classes
# to_pil = transforms.ToPILImage()
# images, labels = get_random_images(5)
# fig=plt.figure(figsize=(10,10))
# for ii in range(len(images)):
#     image = to_pil(images[ii])
#     index = predict_image(image)
#     sub = fig.add_subplot(1, len(images), ii+1)
#     res = int(labels[ii]) == index
#     sub.set_title(str(classes[index]) + ":" + str(res))
#     plt.axis('off')
#     plt.imshow(image)
# plt.show()


#defaults.device = 'cpu'


def test():
    device =  torch.device('cpu')
    testdir = 'food/test2'
    normalize = transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                     std=[0.229, 0.224, 0.225])

    input_size = 224
    test_dataset = torchvision.datasets.ImageFolder(
        testdir,
        transforms.Compose([
            transforms.RandomResizedCrop(input_size, scale=(1.0, 1.0)),
            transforms.ToTensor(),
            normalize,
        ]))

    testloader = torch.utils.data.DataLoader(test_dataset,batch_size=1, shuffle=False,num_workers=4, pin_memory=True)
    device = torch.device('cpu')
    net = MobileNetV2(n_class=2)
    net.load_state_dict(torch.load('checkpoint/ckpt.pth', map_location=device))
    net.eval()
    idx = 0
    result = True
    with torch.no_grad():
        for batch_idx, (inputs, targets) in enumerate(testloader):
            inputs, targets = inputs.to(device), targets.to(device)
            outputs = net(inputs)

            _, predicted = outputs.max(1)

            for i in range(predicted.size()[0]):
                predict_class = 'food' if predicted[i] == 0 else 'no-food'
                target_class = 'food' if targets[i] == 0 else 'no-food'

                print('File: ' + test_dataset.imgs[idx][0])
                print('Prediction: ' + predict_class + ' | Groundtruth: ' + target_class)

                idx += 1
                if predict_class == 'food':
                    result = True
                else:
                    result = False
    return result

final = test()
print(final)
