import React, {Component} from 'react';

import './Notes.css';
import '../common/Common.css'

import CampaignsAPI from '../campaigns/CampaignsAPI';
import Session from '../common/Session';
import RemoveModal from '../common/RemoveModal';

import Button from '@material-ui/core/Button';

import Save from '@material-ui/icons/Save';
import Delete from '@material-ui/icons/Delete';
import Edit from '@material-ui/icons/Edit';

import TextField from '@material-ui/core/TextField';


class Notes extends Component {
  constructor(props) {
    super(props);

    this.state = {
      notes: [],
      showRemoveNoteModal: false,
      selectedNote: {},
    };

    this.updateNotes = this.updateNotes.bind(this);
    this.removeSelectedNote = this.removeSelectedNote.bind(this);
    this.closeRemoveModal = this.closeRemoveModal.bind(this);
  }

  componentDidMount() {
    this.updateNotes();
  }

  removeSelectedNote() {
    var that = this;
    this.closeRemoveModal();

    CampaignsAPI.deleteNote(this.state.selectedNote,
      response => {
        if (response.status === "OK") {
          that.updateNotes()
        }
      });
  }

  closeRemoveModal() {
    this.setState({showRemoveNoteModal: false});
  }

  addNewNote() {
    var notes = this.state.notes.slice();
    for (var i = 0; i < notes.length; i++) {
      if (notes[i].id === 0) { return; }
    }

    notes.unshift({id: 0, message: "", userId: Session.getUser().id, date: new Date().getTime(), editing: true});
    this.setState({'notes': notes});
  }

  prettyDate(dateMS) {
    if (dateMS > 0) {
      const date = new Date(dateMS);
      return date.toLocaleDateString(date) + ' ' + date.toLocaleTimeString(date);
    } else {
      return 'empty';
    }
  }

  updateNotes() {
    CampaignsAPI.getNotes(response => {
      if (response.status === "OK") {
        this.setState({
          notes: response.data,
        });
      }
    });
  }

  deleteNote(note) {
    this.setState({showRemoveNoteModal: true, selectedNote: note})
  }

  saveNote(note) {
    var that = this;

    CampaignsAPI.saveNote(note,
      response => {
        if (response.status === "OK") {
          that.updateNotes()
        }
      });
  }

  editNote(note) {
    var notes = this.state.notes.slice();
    notes[notes.indexOf(note)].editing = !notes[notes.indexOf(note)].editing;

    this.setState({'notes': notes});
  }

  onTextFieldChanged(e, note) {
    var notes = this.state.notes.slice();
    notes[notes.indexOf(note)].message = e.target.value;

    this.setState({'notes': notes});
  }

  render() {
    return (
      <div className='max-width-600'>
        <div className='note-button-wrapper'>
          <Button
            type="button"
            variant="contained"
            color="primary"
            onClick={(e) => this.addNewNote()}
            className="note-add-button">Add new note</Button>
        </div>

        {this.state.notes.map(row => (
          <div className='note-wrapper' key={row.id}>
            <span className='note-date' >{this.prettyDate(row.date)}</span>

            <div className='note-icons-wrapper'>
              <Delete
                className='pointer action-icon'
                color='primary'
                onClick={(e) => this.deleteNote(row)}/>

              {!row.editing && <Edit
                className='pointer'
                color='primary'
                onClick={(e) => this.editNote(row)}/>}

              {row.editing && <Save
                className='pointer'
                color='primary'
                onClick={(e) => this.saveNote(row)}/>}
            </div>

            {!row.editing && <span
              onClick={(e) => this.editNote(row)}
              className='note-message pointer'>{row.message}</span>}

            {row.editing && <TextField
              className="full-width"
              onChange={(e) => this.onTextFieldChanged(e, row)}
              onBlur={(e) => this.saveNote(row)}
              value={row.message}
              placeholder='Enter your note here'
              multiline
              autoFocus
              rowsMax="4"/>}
          </div>
        ))}

        {this.state.showRemoveNoteModal &&
          <RemoveModal
            title="Remove note"
            message="Are you sure you want to remove this note?"
            remove={this.removeSelectedNote}
            close={this.closeRemoveModal} />
        }
      </div>
    )
  }
}

export default Notes;
