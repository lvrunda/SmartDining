'''Train CIFAR10 with PyTorch.'''
from __future__ import print_function

import torch
import torch.nn as nn
import torch.optim as optim
import torch.nn.functional as F
import torch.backends.cudnn as cudnn

import torchvision
import torchvision.transforms as transforms

import os
import argparse

from models import *
from utils import progress_bar


parser = argparse.ArgumentParser(description='PyTorch CIFAR10 Training')
parser.add_argument('--lr', default=0.01, type=float, help='learning rate')
parser.add_argument('--epoch', default=300, type=int, help='total epoch for training')
parser.add_argument('--test', default=0, type=int, help='0 for train and 1 for test')
args = parser.parse_args()

device = 'cuda' if torch.cuda.is_available() else 'cpu'
best_acc = 0  # best test accuracy
start_epoch = 0  # start from epoch 0 or last checkpoint epoch

def adjust_learning_rate(optimizer):
    for param_group in optimizer.param_groups:
        param_group['lr'] = param_group['lr'] * 0.1
        #print("===> Update learning rate:" + str(param_group['lr']))

# Data
print('==> Preparing data..')

traindir = 'food/train'
testdir = 'food/test'

normalize = transforms.Normalize(mean=[0.485, 0.456, 0.406],
                                 std=[0.229, 0.224, 0.225])

input_size = 224
train_dataset = torchvision.datasets.ImageFolder(
    traindir,
    transforms.Compose([
        transforms.RandomResizedCrop(input_size, scale=(0.9, 1.0)),
        transforms.RandomHorizontalFlip(),
        transforms.ToTensor(),
        normalize,
    ]))

trainloader = torch.utils.data.DataLoader(
    train_dataset, batch_size=32, shuffle=True,
    num_workers=4, pin_memory=True)

test_dataset = torchvision.datasets.ImageFolder(
    testdir,
    transforms.Compose([
        transforms.RandomResizedCrop(input_size, scale=(1.0, 1.0)),
        transforms.ToTensor(),
        normalize,
    ]))

testloader = torch.utils.data.DataLoader(test_dataset,batch_size=1, shuffle=False,num_workers=4, pin_memory=True)

# Model
print('==> Building model..')

net = MobileNetV2(n_class=2)

if not args.test:
    loaded_state_dict = torch.load('checkpoint/mobilenet_v2.pth.tar')
    init_state_dict = net.state_dict()

    from collections import OrderedDict
    my_state_dict = OrderedDict()

    print('===> Load model from pretrained ImageNet model')

    for k, v in loaded_state_dict.items():
        if('classifier.1' in k):
            #my_state_dict[k] = init_state_dict[k] # if imcompatible layer, use Initailized weight
            pass
        else:
            my_state_dict[k] = v # if imcompatible, use old weights

    for k, v in init_state_dict.items():
        if('classifier.1' in k):
            my_state_dict[k] = init_state_dict[k]

    net.load_state_dict(my_state_dict)
else:
    device = torch.device('cpu')
    net = MobileNetV2(n_class=2)
    net.load_state_dict(torch.load('checkpoint/ckpt.pth', map_location=device))
    #net = torch.load('checkpoint/ckpt.pth')

net = net.to(device)
if device == 'cuda':
    #net = torch.nn.DataParallel(net)
    cudnn.benchmark = True

params_net = []
for child in net.children():
    for name, param in net.named_parameters():
        if('classifier.1' in name):
            params_net.append(param)
            param.requires_grad = True
            print('===> Layer for finetuning: ' + name)
        else:
            ####### Set False to fix weights
            param.requires_grad = False

params_list = [{'params': filter(lambda p: p.requires_grad, params_net), 'lr': args.lr}]

criterion = nn.CrossEntropyLoss()
optimizer = optim.Adam(params_list, lr=args.lr, betas=(0.9, 0.999))

# Training
def train(epoch):
    net.train()
    train_loss = 0
    correct = 0
    total = 0
    for batch_idx, (inputs, targets) in enumerate(trainloader):
        inputs, targets = inputs.to(device), targets.to(device)
        optimizer.zero_grad()
        outputs = net(inputs)
        loss = criterion(outputs, targets)
        loss.backward()
        optimizer.step()

        train_loss += loss.item()
        _, predicted = outputs.max(1)
        total += targets.size(0)
        correct += predicted.eq(targets).sum().item()

        progress_bar(batch_idx, len(trainloader), 'Loss: %.3f | Acc: %.3f%% (%d/%d)'
            % (train_loss/(batch_idx+1), 100.*correct/total, correct, total))
        #print("===>Train[{}] [{}/{}]: Loss[{:.3f}] Acc[{:.3f}] ({}/{})".format(epoch, batch_idx,len(trainloader), train_loss/(batch_idx+1), 100.*correct/total, correct, total))

def test(epoch):
    idx = 0
    global best_acc
    net.eval()
    test_loss = 0
    correct = 0
    total = 0
    with torch.no_grad():
        for batch_idx, (inputs, targets) in enumerate(testloader):
            inputs, targets = inputs.to(device), targets.to(device)
            outputs = net(inputs)

            loss = criterion(outputs, targets)

            test_loss += loss.item()
            _, predicted = outputs.max(1)

            if args.test:
                for i in range(predicted.size()[0]):
                    predict_class = 'food' if predicted[i] == 0 else 'no-food'
                    target_class = 'food' if targets[i] == 0 else 'no-food'

                    print('File: ' + test_dataset.imgs[idx][0])
                    print('Prediction: ' + predict_class + ' | Groundtruth: ' + target_class)
                    idx += 1

            total += targets.size(0)
            correct += predicted.eq(targets).sum().item()

            progress_bar(batch_idx, len(testloader), 'Loss: %.3f | Acc: %.3f%% (%d/%d)'
            % (test_loss/(batch_idx+1), 100.*correct/total, correct, total))

            #print("===>Test[{}] [{}/{}]: Loss[{:.3f}] Acc[{:.3f}] ({}/{})".format(epoch, batch_idx, len(testloader), test_loss/(batch_idx+1), 100.*correct/total, correct, total))

    # Save checkpoint.
    if not args.test:
        acc = 100.*correct/total
        if acc > best_acc:
            print('Saving..')
            if not os.path.isdir('checkpoint'):
                os.mkdir('checkpoint')
            torch.save(net.state_dict(), './checkpoint/ckpt.pth')
            best_acc = acc


if not args.test:
    for epoch in range(1, args.epoch+1):
        train(epoch)
        test(epoch)

        if epoch % 100 == 0:
            adjust_learning_rate(optimizer)
else:
    test(epoch=1)
