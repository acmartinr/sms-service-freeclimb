import React, {Component} from 'react';
import {isMobile} from 'react-device-detect';

import './Chat.css';
import queryString from 'query-string';
import Button from '@material-ui/core/Button';

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import CircularProgress from '@material-ui/core/CircularProgress';
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import FormControl from '@material-ui/core/FormControl';
import InputLabel from '@material-ui/core/InputLabel';

import TextField from '@material-ui/core/TextField';
import { withRouter } from 'react-router-dom'

import Send from '@material-ui/icons/Send';
import Delete from '@material-ui/icons/Delete';
import NotInterested from '@material-ui/icons/NotInterested';
import ImportExport from '@material-ui/icons/GetApp';

import TablePagination from '@material-ui/core/TablePagination';

import RemoveModal from '../common/RemoveModal';

class Chat extends Component {

  constructor(props) {
    super(props);

    this.state = {
      chats: [],
      count: 0,
      rowsPerPage: 20,
      page: 0,
      messages: [],
      selectedChat: {},
      message: '',
      loading: false,
      sendLoading: false,
      chatUpdateIntervalId: '',
      phones: [],
      phoneFrom: '',
      sendError: false,
      errorMessage: '',
      showRemoveChatModal: '',
      chatForRemoving: '',
      removeMessage: '',
      forbiddenMessage: '',
      forbiddenWords: [],//'loan','cash','$','!',/*'student',*/'debt', 'cost', 'price', 'gov', 'fed', 'covid', 'corona', 'virus', 'pandemic', 'crisis', 'quarantine', 'stimulus'],
      messagesHeight: (window.innerHeight - 74)/(isMobile ? 2 : 1),
      chatsHeight: (window.innerHeight - 74)/(isMobile ? 2 : 1),
      messagesWithoutInputHeight: (window.innerHeight - 74)/(isMobile ? 2 : 1) - (isMobile ? 132 : 66),
      queryPhoneFrom: ''
    }

    this.onChatSelected = this.onChatSelected.bind(this);
    this.updateChats = this.updateChats.bind(this);
    this.handleChange = this.handleChange.bind(this);
    this.banChatPhone = this.banChatPhone.bind(this);
    this.removeChat = this.removeChat.bind(this);
    this.removeSelectedChat = this.removeSelectedChat.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);

    this.handleChangePage = this.handleChangePage.bind(this);
    this.handleChangeRowsPerPage = this.handleChangeRowsPerPage.bind(this);
  }

  componentDidMount() {
    this.updatePhones();

    if (!this.getPhoneFromParameter()) {
      this.updateChats();
      this.scheduleChatUpdate();
    } else {
      this.requestPhoneChat();
    }
  }

  getPhoneFromParameter() {
    var parsedQuery = queryString.parse(this.props.location.search);
    return parsedQuery.phoneFrom;
  }

  handleChangePage(event, value) {
    this.setState({page: value, selectedChat: {}, messages: []}, this.updateChats);
  }

  handleChangeRowsPerPage(event) {
    this.setState({rowsPerPage: event.target.value, page: 0}, this.updateChats);
  }

  updatePhones() {
    const request = {
      userId: Session.getUser().id,
      page: 0,
      limit: 1000
    };

    CampaignsAPI.getPhones(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          phones: response.data.phones,
          phoneFrom: response.data.phones.length > 0 ? response.data.phones[0].phone : ''
        });
      }
    });
  }

  isLimitedUser() {
    return Session.getUser().role === 2;
  }

  scheduleChatUpdate() {
    const that = this;
    const chatUpdateIntervalId = setInterval(function() {
      that.updateChats();

      if (that.state.selectedChat && that.state.selectedChat.id) {
        that.updateChatMessages(that.state.selectedChat);
      }
    }, 10000);
    this.setState({'chatUpdateIntervalId': chatUpdateIntervalId});
  }

  requestPhoneChat() {
    this.setState({loading: true, queryPhoneFrom: this.getPhoneFromParameter()});

    const request = {
      userId: Session.getUser().id,
      phoneFrom: this.getPhoneFromParameter(),
      limit: 1,
      page: 0
    }

    CampaignsAPI.getChats(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          chats: response.data.chats,
          count: response.data.count,
          loading: false});

        if (response.data.chats.length > 0) {
          this.onChatSelected(response.data.chats[0], {});
        }
      }
    });
  }

  showAllChats() {
    this.setState({queryPhoneFrom: ''});
    this.updateChats();
    this.scheduleChatUpdate();
  }

  componentWillUnmount() {
    if (this.state.chatUpdateIntervalId) {
      clearInterval(this.state.chatUpdateIntervalId);
    }
  }

  updateChats() {
    this.setState({loading: true});

    const request = {
      userId: Session.getUser().id,
      page: this.state.page,
      limit: this.state.rowsPerPage
    }

    CampaignsAPI.getChats(request,
    response => {
      if (response.status === "OK") {
        this.setState({
          chats: response.data.chats,
          count: response.data.count,
          loading: false});
      }
    });
  }

  removeChat(chat) {
    var that = this;
    this.setState({
      //'showRemoveChatModal': true,
      //removeMessage: 'Do you really want to remove chat with +' + chat.phoneTo,
      chatForRemoving: chat}, function() {that.removeSelectedChat()});
  }

  removeSelectedChat() {
    CampaignsAPI.removeChat(this.state.chatForRemoving,
    response => {
      if (response.status === "OK") {
        this.updateChats();
        if (this.state.selectedChat && this.state.selectedChat.id === this.state.chatForRemoving.id) {
          this.setState({selectedChat: {}, messages: []});
        }

        this.setState({showRemoveChatModal: false, chatForRemoving: {}});
      }
    });
  }

  closeRemoveModal() {
    this.setState({showRemoveChatModal: false});
  }

  banChatPhone(chat) {
    CampaignsAPI.banChatPhone(chat,
    response => {
      if (response.status === "OK") {
        this.updateChats();
        if (this.state.selectedChat && this.state.selectedChat.id === chat.id) {
          this.setState({selectedChat: {}, messages: []});
        }
      }
    });
  }

  onChatSelected(chat, event) {
    if (event.target && (event.target.classList.contains("chat-icon") ||
      (event.target.parentNode && event.target.parentNode.classList.contains("chat-icon")))) {
      return;
    }

    this.setState({selectedChat: chat, messages: [], message: ''});

    var chats = this.state.chats.slice();
    chats[this.findChatIndex(chat, chats)].read = true;
    this.setState({'chats': chats});

    var that = this;
    this.updateChatMessages(chat, function() {
      if (that.state.messages.length > 0) {
        for (var j = that.state.messages.length - 1; j > -1 ; j--) {
          var message = that.state.messages[j];

          if (message.inbound === false) {
            for (var i = 0; i < that.state.phones.length; i++) {
              if (that.state.phones[i].phone === message.phoneFrom) {
                that.setState({phoneFrom: message.phoneFrom});
                return;
              }
            }
          }
        }
      }
    });
  }

  findChatIndex(chat, chats) {
    for (var i = 0; i < chats.length; i++) {
      if (chat.id === chats[i].id) {
        return i;
      }
    }

    return 0;
  }

  updateChatMessages(chat, callback) {
    CampaignsAPI.getChatMessages(chat,
    response => {
      if (response.status === "OK") {
        this.setState({messages: response.data}, function() {
          const messagesWrapper = document.getElementById("messages-inner-wrapper");

          if (messagesWrapper.scrollTo) {
            messagesWrapper.scrollTo(0, messagesWrapper.scrollHeight);
          } else {
            messagesWrapper.scrollTop = messagesWrapper.scrollHeight;
          }
        });

        if (callback) { callback(); }
      }
    });
  }

  sendMessage() {
    if (this.state.message.length === 0 || this.state.messages.length === 0 || !this.state.phoneFrom) {
      return
    }

    var forbiddenWords = [];
    for (var i = 0; i < this.state.forbiddenWords.length; i++) {
      if (this.state.message.toLowerCase().indexOf(this.state.forbiddenWords[i].toLowerCase()) !== -1) {
        forbiddenWords.push(this.state.forbiddenWords[i]);
      }
    }

    if (forbiddenWords.length > 0) {
      var words = '';
      for (var j = 0; j < forbiddenWords.length; j++) {
        words = words + "\"" + forbiddenWords[j] + "\"";
        if (j < forbiddenWords.length - 1) {
          words = words + ', ';
        }
      }

      this.setState({forbiddenMessage:
        'You use forbidden words in your message: ' + words + '.'});
        return;
    }

    const request = {
      chatId: this.state.selectedChat.id,
      message: this.state.message,
      phoneFrom: this.state.phoneFrom
    };

    this.setState({sendLoading: true, sendMessage: '', sendError: false});
    CampaignsAPI.sendMessage(request,
    response => {
      if (response.status === "OK") {
        this.setState({message: '', sendLoading: false});
        this.updateChatMessages(this.state.selectedChat);
      } else if (response.message === 'balance') {
        this.setState({sendError: true, errorMessage: 'Your balance is too low', sendLoading: false});
      }
    });
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    }
  }

  onTextFieldChanged(event, field) {
    this.setState({
      sendError: false,
      forbiddenMessage: false,
      [field]: event.target.value
    });
  }

  handleChange(event) {
    this.setState({
      [event.target.name]: event.target.value
    });
  }

  exportChats() {
    CampaignsAPI.exportChats(function(response) {
      if (response.status === 'OK') {
        var path = '/api/chats/export/' + response.message;
        var frame = document.createElement('iframe');
        frame.setAttribute('src', path);
        frame.style.width = 0;
        frame.style.height = 0;
        document.body.appendChild(frame);
      }
    });
  }

  getTextFieldLabel() {
    if (this.state.sendError) {
      return this.state.errorMessage;
    } else if (this.state.forbiddenMessage) {
      return this.state.forbiddenMessage;
    }

    return "Enter your message";
  }

  render() {
    return (
      <div>
        <div style={{height: this.state.chatsHeight}} className={isMobile ? "chats-wrapper-mobile" : "chats-wrapper"}>
          {!this.state.chats.length && !this.state.loading && <span className='info-message empty-chats-message'>You don't have any chats yet</span>}

          {this.state.chats.length > 0 &&
            <TablePagination
              className={isMobile ? "chats-pagination-mobile" : "chats-pagination"}
              rowsPerPageOptions={[20, 50, 100]}
              labelRowsPerPage="Count:"
              component="div"
              count={this.state.count}
              rowsPerPage={this.state.rowsPerPage}
              page={this.state.page}
              backIconButtonProps={{
                'aria-label': 'Previous Page',
              }}
              nextIconButtonProps={{
                'aria-label': 'Next Page',
              }}
              onChangePage={this.handleChangePage}
              onChangeRowsPerPage={this.handleChangeRowsPerPage}
            />
          }

          {(!isMobile && this.state.chats.length > 0) && <ImportExport
            onClick={(e) => this.exportChats()}
            className={this.isLimitedUser() ? "export-chat-limited-icon" : "export-chat-icon"} color="primary"/>}

          <div className="chat-global-wrapper">
            {this.state.chats.map((row) => (
              <div className={row.id === this.state.selectedChat.id ? (isMobile ? 'chat-wrapper-mobile-selected' : 'chat-wrapper-selected') : (isMobile ? 'chat-wrapper-mobile' : 'chat-wrapper')}
                   onClick={(e) => this.onChatSelected(row, e)} key={row.id}>
                <span className='chat-phone'>[+{row.phoneTo}]</span>
                <span className='chat-date'>{this.prettyDate(row.lastDate)}</span>

                <NotInterested
                  onClick={(e) => this.banChatPhone(row)}
                  className="chat-icon" color="primary"/>
                <Delete
                  onClick={(e) => this.removeChat(row)}
                  className="chat-icon" color="primary"/>

                <br/>{row.carrier && <span className='chat-phone'>[{row.carrier}]</span>}
                <p className='chat-message'>
                  {!row.read && <span className='chat-new-label'>New</span>}
                  {row.lastMessage}
                </p>
              </div>
            ))}

            {this.state.queryPhoneFrom &&
              <Button
                type="button"
                variant="contained"
                color="primary"
                onClick={(e) => this.showAllChats()}
                className="login-submit">
                Show all chats
              </Button>
            }
          </div>
        </div>
        <div style={{height: this.state.messagesHeight}} className={isMobile ? "messages-wrapper-mobile" : "messages-wrapper"}>
          <div style={{height: this.state.messagesWithoutInputHeight}} id="messages-inner-wrapper"
               className="messages-inner-wrapper padding-top-10">
            {!this.state.selectedChat.id && <span className='info-message'>Select chat to view messages</span>}
            {this.state.messages.map((row) => (
              <div key={row.id}
                   className={!row.inbound ? 'chat-bubble-wrapper-inbound' : 'chat-bubble-wrapper'}>
                {!row.inbound && <span className='chat-message-phone'>[+{row.phoneFrom}]</span>}
                <span className='chat-message-date'>{this.prettyDate(row.date)}</span>
                <br/>
                <div className={row.inbound ? 'chat-bubble-inbound' : 'chat-bubble'}>
                  <span>{row.message}</span>
                </div>
              </div>
            ))}
          </div>

          <div className="send-message-wrapper">
            <TextField
              autoFocus
              id="message"
              error={this.state.sendError || this.state.forbiddenMessage.length > 0}
              label={this.getTextFieldLabel()}
              type="text"
              disabled={this.state.messages.length === 0 || this.state.sendLoading}
              value={this.state.message}
              className={isMobile ? 'message-text-field-mobile' : 'message-text-field'}
              onChange={(e) => this.onTextFieldChanged(e, "message")}
            />

            <FormControl className={isMobile ? 'message-phone-field-mobile' : 'message-phone-field'}>
              <InputLabel htmlFor="phoneFrom">Caller ID</InputLabel>
              <Select
                value={this.state.phoneFrom}
                disabled={this.state.messages.length === 0 || this.state.sendLoading}
                onChange={this.handleChange}
                inputProps={{name: 'phoneFrom', id: 'phoneFrom'}}>
                {this.state.phones.map(row => (
                  <MenuItem key={row.id} value={row.phone}>+{row.phone}</MenuItem>
                ))}
              </Select>
            </FormControl>

            {!this.state.sendLoading &&
              <Send
                className='pointer chat-send-button'
                color='primary'
                disabled={this.state.message.length === 0 || this.state.messages.length === 0}
                onClick={(e) => this.sendMessage()}/>
            }

            {this.state.sendLoading && <CircularProgress className="custom-progress"/>}
          </div>
        </div>

        {this.state.showRemoveChatModal &&
          <RemoveModal
            title="Chat remove"
            message={this.state.message}
            remove={this.removeSelectedChat}
            close={this.closeRemoveModal} />
        }
      </div>
    )
  }
}

export default withRouter(Chat);
